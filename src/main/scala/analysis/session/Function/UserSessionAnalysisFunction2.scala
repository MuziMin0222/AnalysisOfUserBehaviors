package analysis.session.Function

import commons.constant.Constants
import commons.utils.{DateUtils, StringUtils}
import org.apache.spark.rdd.RDD

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random
import analysis.session._
import analysis.session.bean.{SessionDetail, SessionRandomExtract}
import commons.conf.ConfigurationManager
import org.apache.spark.sql.SaveMode

import scala.collection.immutable.StringOps

/**
 * 需求二：Session随机抽取
 *
 * 在符合过滤条件的session中，按照时间比例随机抽取100个session。当存在若干天的数据时，100个session抽取指标在天之间平均分配，
 * 在一天之中，根据某个小时的session数量在一天中总session数量中的占比决定这个小时抽取多少个session。
 *
 * 明确一共要抽取多少session    100条session
 * 明确每天要抽取多少session    一共有多少天的数据，从一共取多少条session平均每天进行分配
 * 明确每天有多少session         进行统计计算
 * 明确每小时有多少session        按小时进行分
 * 明确每小时抽取多少session      一个小时要抽取的session数量 = (这个小时的session数量/这一天的session数量) * 这一天要抽取的session数量
 * 根据每小时抽取数量生成随机索引
 * 按照随机索引抽取实际的一个小时中的session
 */
object UserSessionAnalysisFunction2 {

  def Demand2() = {
    val tuple = UserSessionAnalysisFunction1.Demand1()
    //(b5368124e22d45af85c1c5a38ab9be4c,sessionid=b5368124e22d45af85c1c5a38ab9be4c|searchKeywords=新辣道鱼火锅,温泉,太古商场,呷哺呷哺,日本料理,重庆小面|clickCategoryIds=83,23,43,63,56,46,27,41,21,57,93,51|visitLength=3491|stepLength=53|startTime=2020-01-08 12:00:33|age=31|professional=professional90|city=city98|sex=female)
    val session_id2AggrInfoRDD: RDD[(String, String)] = tuple._1

    //计算出每天每小时的session数量，获取<yyyy-MM-dd_HH,aggrInfo>格式的RDD
    val time2session_idRDD: RDD[(String, String)] = session_id2AggrInfoRDD.map {
      case (session_id, aggrinfo) => {
        val startTime: String = StringUtils.getFieldFromConcatString(aggrinfo, "\\|", Constants.FIELD_START_TIME)

        //将startTime作为Key，并将格式修改为YYYY-MM-DD_HH形式
        val dateHour: String = DateUtils.getDateHour(startTime)

        (dateHour, aggrinfo)
      }
    }

    //得到每天每小时的session数量，统计countByKey()计算每个不同的key有多少个数据，countMap -> (yyyy-MM-dd_HH,count)
    val countMap: collection.Map[String, Long] = time2session_idRDD.countByKey()

    //第二步，使用按时间比例随机抽取算法，计算出每天每小时要抽取session的索引，将<yyyy-MM-dd_HH,count>格式的map，转换成<yyyy-MM-dd,<HH,count>>的格式
    //dateHourCountMap <yyyy-MM-dd,<HH,count>>
    //(2020-01-14,Map(12 -> 94, 15 -> 102, 00 -> 110, 09 -> 96, 21 -> 92, 18 -> 106, 03 -> 114, 06 -> 82, 17 -> 80, 11 -> 92, 05 -> 124, 08 -> 82, 14 -> 78, 20 -> 92, 02 -> 84, 22 -> 96, 01 -> 98, 16 -> 86, 04 -> 78, 10 -> 108, 19 -> 114, 13 -> 106, 07 -> 86))
    val dateHourCountMap = mutable.HashMap[String, mutable.HashMap[String, Long]]()
    for ((dateHour, count) <- countMap) {
      val dateArr = dateHour.split("_")
      val date: String = dateArr(0)
      val hour: String = dateArr(1)

      //通过模式匹配实现if的功能
      dateHourCountMap.get(date) match {
        //对应日期不存在，就新增
        case None => {
          dateHourCountMap(date) = new mutable.HashMap[String, Long]()
          dateHourCountMap(date) += (hour -> count)
        }
        //对应日期存在，则更新
        case Some(hourCountMap) => {
          hourCountMap += (hour -> count)
        }
      }
    }

    //按时间比例随机抽取算法，总共要抽取100个session，先按照天数，进行平分
    //获取每天要抽取的数据量
    val extractNumberPerDay = 100 / dateHourCountMap.size

    //dateHourExtractMap -> (day,(hour,index))
    val dateHourExtractMap = mutable.HashMap[String, mutable.HashMap[String, ListBuffer[Int]]]()

    val random = new Random()

    //session随机抽取功能,执行完该for循环后，index就获取完毕啦
    for ((date, hourCountMap) <- dateHourCountMap) {
      //计算这一天的session总数
      val sessionCount = hourCountMap.values.sum

      //dateHourExtractMap[天,[小时，小时列表]]
      dateHourExtractMap.get(date) match {
        case None => {
          dateHourExtractMap(date) = new mutable.HashMap[String, mutable.ListBuffer[Int]]()
          hourExtractMapFunc(dateHourExtractMap(date), hourCountMap, sessionCount)
        }
        case Some(hourExtractMap) => {
          hourExtractMapFunc(hourExtractMap, hourCountMap, sessionCount)
        }
      }
    }

    /**
     * 根据每小时应该抽取的数量，来产生随机值
     *
     * @param hourExtractMap 存放生成的随机值
     * @param hourCountMap   每个小时的session总数
     * @param sessionCount   当天所有的session总数
     */
    def hourExtractMapFunc(hourExtractMap: mutable.HashMap[String, mutable.ListBuffer[Int]],
                           hourCountMap: mutable.HashMap[String, Long],
                           sessionCount: Long
                          ) = {
      for ((hour, count) <- hourCountMap) {
        //计算每个小时的session数量，占据当天总session数量的比例，直接乘以每天要抽取的数量，就是当前小时需要抽取的session数量
        var hourExtractNumber = ((count / sessionCount.toDouble) * extractNumberPerDay).toInt

        //如果当前小时要抽取的数量比该小时总的session数量还要大，那么直接拿该小时所有的session数量
        if (hourExtractNumber > count) {
          hourExtractNumber = count.toInt
        }

        hourExtractMap.get(hour) match {
          case None => {
            hourExtractMap(hour) = new mutable.ListBuffer[Int]()
            //根据数量随机生成下标
            for (i <- 0 to hourExtractNumber) {
              var extractIndex = random.nextInt(count.toInt)

              //随机生成的index可以会重复，所以需要进行重新获取
              while (hourExtractMap(hour).contains(extractIndex)) {
                extractIndex = random.nextInt(count.toInt)
              }
              hourExtractMap(hour) += extractIndex
            }
          }
        }
      }
    }

    //广播处理
    val dateHourExtracMapBD = sc.broadcast(dateHourExtractMap)

    //time2session_idRDD：（yyyy-MM-dd_HH,aggrInfo）
    val time2sessionsRDD = time2session_idRDD.groupByKey()

    //遍历每天每小时的session，然后根据随机索引进行抽取
    val sessionRandomExtract = time2sessionsRDD.flatMap {
      case (dateHour, items) => {
        val date = dateHour.split("_")(0)
        val hour: StringOps = dateHour.split("_")(1)

        //从广播变量中提取数据
        val dateHourExtractMap = dateHourExtracMapBD.value

        //获取指定天对应的指定小时的indexList，当前小时对应的index集合
        val extractIndexList = dateHourExtractMap.get(date).get(hour)

        var index = 0
        val sessionRandomExtractArray = new ArrayBuffer[SessionRandomExtract]()
        for (sessionAggrInfo <- items) {
          //如果筛选的List中包含当前的index，则提取sessionAggrInfo中的数据
          if (extractIndexList.contains(index)) {
            val session_id = StringUtils.getFieldFromConcatString(sessionAggrInfo, "\\|", Constants.FIELD_SESSION_ID)
            val start_time = StringUtils.getFieldFromConcatString(sessionAggrInfo, "\\|", Constants.FIELD_START_TIME)
            val searchKeyWords = StringUtils.getFieldFromConcatString(sessionAggrInfo, "\\|", Constants.FIELD_SEARCH_KEYWORDS)
            val clickCategoryIds = StringUtils.getFieldFromConcatString(sessionAggrInfo, "\\|", Constants.FIELD_CLICK_CATEGORY_IDS)
            sessionRandomExtractArray += SessionRandomExtract(taskUUID, session_id, start_time, searchKeyWords, clickCategoryIds)
          }
          index += 1
        }
        sessionRandomExtractArray
      }
    }

    //将数据保存到Mysql中
    import spark.implicits._
    sessionRandomExtract.toDF()
      .write
      .format("jdbc")
      .option("url", ConfigurationManager.config.getString(Constants.JDBC_URL))
      .option("dbtable", ConfigurationManager.config.getString(Constants.JDBC_TABLE_SESSIONRANDOMEXTRACT))
      .option("user", ConfigurationManager.config.getString(Constants.JDBC_USER))
      .option("password", ConfigurationManager.config.getString(Constants.JDBC_PASSWORD))
      .mode(SaveMode.Overwrite)
      .save()

    //提取抽取出来的数据中的session_id
    val extractSession_idsRDD = sessionRandomExtract.map(item => {
      (item.sessionid, item.sessionid)
    })

    //获取抽取出来的session的明细数据
    val extractSessionDetailRDD = extractSession_idsRDD.join(tuple._3)

    //对extractSessionDetailRDD中的数据进行聚合，提炼有价值的明细数据
    val sessionDetailRDD = extractSessionDetailRDD.map {
      case (sid, (session_id, userVisitAction)) => {
        SessionDetail(
          taskUUID,
          userVisitAction.user_id,
          userVisitAction.session_id,
          userVisitAction.page_id,
          userVisitAction.action_time,
          userVisitAction.search_keyword,
          userVisitAction.click_category_id,
          userVisitAction.click_product_id,
          userVisitAction.order_category_ids,
          userVisitAction.order_product_ids,
          userVisitAction.pay_category_ids,
          userVisitAction.pay_product_ids
        )
      }
    }

    //数据保存到数据库中
    sessionDetailRDD.toDF()
      .write
      .format("jdbc")
      .option("url", ConfigurationManager.config.getString(Constants.JDBC_URL))
      .option("dbtable", ConfigurationManager.config.getString(Constants.JDBC_TABLE_SESSIONDETAIL))
      .option("user", ConfigurationManager.config.getString(Constants.JDBC_USER))
      .option("password", ConfigurationManager.config.getString(Constants.JDBC_PASSWORD))
      .mode(SaveMode.Overwrite)
      .save()
  }
}