package analysis.session.Function

import analysis._
import commons.constant.Constants
import commons.utils.StringUtils
import org.apache.spark.rdd.RDD
import analysis.session.bean.{SessionDetail, Top10Session}
import commons.conf.ConfigurationManager
import org.apache.spark.sql.SaveMode

import scala.collection.mutable

/**
 * 需求四：Top10热门品类的Top10活跃Session统计
 *
 * 统计需求三中得到的Top10热门品类中的Top10活跃Session，对Top10热门品类中的每个品类都取Top10活跃Session，
 * 评判活跃Session的指标是一个Session对一个品类的点击次数。
 */
object UserSessionAnalysisFunction4 {
  private val tuple = UserSessionAnalysisFunction3.demand3()
  private val top10CategoryList: Array[(CategorySoryKey, String)] = tuple._1
  private val session_id2detailRDD = tuple._2

  def deamnd4() = {
    //1、将top10的热门商品品类的ID，生成一份RDD
    //获取所有需要的category集合
    val top10CategoryIdRDD: RDD[(Long, Long)] = sc.makeRDD(top10CategoryList).map {
      case (cateforySortKey, line) => {
        val category_id = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_CATEGORY_ID).toLong
        (category_id, category_id)
      }
    }

    //2、计算top10 品类被各session点击的次数
    val sessionid2ActionsRDD = session_id2detailRDD.groupByKey()

    //获取每个品类被每一个session点击的次数
    val categoryId2SessionCounrRDD: RDD[(Long, String)] = sessionid2ActionsRDD.flatMap {
      case (sessionid, userVisitActions) => {
        val categoryCountMap = new mutable.HashMap[Long, Long]()

        //遍历userVisitActions是提取session中的每一个用户行为，并对每一个用户行为中的点击事件进行计数
        for (action <- userVisitActions) {
          if (!categoryCountMap.contains(action.click_category_id)) {
            categoryCountMap += ((action.click_category_id, 0))
          }
          if (action.click_category_id != -1L) {
            categoryCountMap.update(action.click_category_id, categoryCountMap(action.click_category_id) + 1)
          }
        }

        //对categoryCountMap中的数据进行格式转化
        for ((cateforyId, count) <- categoryCountMap)
        //yield会记住每次for循环迭代的值，根据自己的算法把数据放入到原集合中
          yield (cateforyId, sessionid + "," + count)
      }
    }

    //通过top10热门品类top10CategoryIdRDD与完整品类点击统计category2SessionCountRDD进行join，仅获取热门品类的数据信息
    val top10CategorySessionCountRDD = top10CategoryIdRDD.join(categoryId2SessionCounrRDD).map {
      case (cid, (ccid, value)) => {
        (cid, value)
      }
    }

    //3、分组去topN的实现，获取每个品类的top10活跃用户
    //先安装商品类别分组,再将每一个品类的所有点击排序，取前十个，并转为对象
    val top10SessionObjectRDD: RDD[Top10Session] = top10CategorySessionCountRDD
      .groupByKey()
      .flatMap {
        case (category_Id, clicks) => {
          //先排序，然后再去前十
          val top10Sessions: List[String] = clicks.toList.sortWith(_.split(",")(1) > _.split(",")(1)).take(10)

          top10Sessions.map {
            case line => {
              val session_id: String = line.split(",")(0)
              val count: Long = line.split(",")(1).toLong

              Top10Session(taskUUID, category_Id, session_id, count)
            }
          }
        }
      }

    //将结果以追加方式写入到MySQL中
    import spark.implicits._
    top10SessionObjectRDD.toDF().write
      .format("jdbc")
      .option("url", ConfigurationManager.config.getString(Constants.JDBC_URL))
      .option("dbtable", ConfigurationManager.config.getString(Constants.JDBC_TABLE_TOP10SESSION))
      .option("user", ConfigurationManager.config.getString(Constants.JDBC_USER))
      .option("password", ConfigurationManager.config.getString(Constants.JDBC_PASSWORD))
      .mode(SaveMode.Overwrite)
      .save()

    val top10SessionRDD: RDD[(String, String)] = top10SessionObjectRDD.map(item => {
      (item.sessionid, item.sessionid)
    })

    //4、获取top0活跃的session的明细数据
    val sessionDetailRDD: RDD[SessionDetail] = top10SessionRDD.join(session_id2detailRDD).map {
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
    //将明细数据写到数据库中
    sessionDetailRDD.toDF().write
      .format("jdbc")
      .option("url",ConfigurationManager.config.getString(Constants.JDBC_URL))
      .option("dbtable",ConfigurationManager.config.getString(Constants.JDBC_TABLE_SESSIONDETAIL))
      .option("user",ConfigurationManager.config.getString(Constants.JDBC_USER))
      .option("password",ConfigurationManager.config.getString(Constants.JDBC_PASSWORD))
      .mode(SaveMode.Append)
      .save()
  }
}
