package analysis.session.Function

import java.util.Date

import analysis._
import analysis.session.bean.SessionAggrStat
import commons.conf.ConfigurationManager
import commons.constant.Constants
import commons.model.{UserInfo, UserVisitAction}
import commons.utils._
import net.sf.json.JSONObject
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.util.AccumulatorV2

import scala.collection.mutable

/**
 * 需求一中会用到的函数方法
 */
object Demand1Function {
  /**
   * 根据日期获取对象的用户行为数据
   *
   * @param taskParm JSON对象
   * @return 从表中查询的到的数据封装的RDD
   */
  def getActionRDDByDateRange(taskParm: JSONObject) = {
    val startDate = ParamUtils.getParam(taskParm, Constants.PARAM_START_DATE)
    val endDate = ParamUtils.getParam(taskParm, Constants.PARAM_END_DATE)

    val sql = "select * from db_userbehaviors.user_visit_action where date >= '" + startDate + "' and date <= '" + endDate + "'"
    val user_visit_actionDF: DataFrame = spark.sql(sql)

    import spark.implicits._
    user_visit_actionDF.as[UserVisitAction].rdd
  }

  /**
   * 以session_id 为可以对行为表和用户信息表进行聚合操作
   *
   * @param sessionId2actionRDD 以session_id为key，用户行为对象集合为value的rdd
   * @return （session_id,用户动作和用户信息组成的字符串）二元元组
   */
  def aggregateBySession(sessionId2actionRDD: RDD[(String, UserVisitAction)]) = {

    //以session_id作为key进行分组操作
    //(c7f19806dd274ba9a77be2f640a76cd9,CompactBuffer(UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,4,2020-01-07 7:31:41,null,-1,-1,24,63,null,null,0), UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,71,2020-01-07 7:56:01,null,-1,-1,9,61,null,null,1), UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,11,2020-01-07 7:38:07,null,-1,-1,null,null,93,37,6), UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,45,2020-01-07 7:58:40,null,-1,-1,null,null,33,93,6), UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,20,2020-01-07 7:48:17,null,-1,-1,73,37,null,null,3), UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,45,2020-01-07 7:36:11,null,17,30,null,null,null,null,3), UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,90,2020-01-07 7:43:43,null,-1,-1,86,52,null,null,1), UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,84,2020-01-07 7:13:41,null,-1,-1,31,64,null,null,0), UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,67,2020-01-07 7:49:18,null,62,0,null,null,null,null,2), UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,26,2020-01-07 7:15:16,null,-1,-1,61,54,null,null,4), UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,20,2020-01-07 7:07:38,null,-1,-1,null,null,5,9,0), UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,47,2020-01-07 7:41:17,null,2,57,null,null,null,null,0)))
    val session_id2ActionsRDD = sessionId2actionRDD.groupByKey()

    //(88,sessionid=ee49f320fe7849a99afebca08d1425f8|searchKeywords=呷哺呷哺,日本料理,国贸大厦,重庆辣子鸡,新辣道鱼火锅,温泉,重庆小面,蛋糕|clickCategoryIds=81,99,0,56,96,65,61,10,24,13,60,41,52,90,57|visitLength=3361|stepLength=68|startTime=2020-01-07 13:01:35)
    val user_id2PartAggrInfoRDD = session_id2ActionsRDD.map {
      case (session_id, userVisitActions) => {
        //搜索关键字的集合
        val searchKeyWordsBuffer = new StringBuffer("")
        //点击类别集合
        val clickCategoryIdsBuffer = new StringBuffer("")

        var user_id = -1L

        //session的起止时间
        var startTime: Date = null
        var endTime: Date = null

        //session的访问步长，即一个session中action的个数
        var stepLength = 0;

        //遍历session所有的访问行为
        userVisitActions.foreach {
          //userVisitAction : UserVisitAction(2020-01-07,16,c7f19806dd274ba9a77be2f640a76cd9,4,2020-01-07 7:31:41,null,-1,-1,24,63,null,null,0)
          userVisitAction => {
            if (user_id == -1L) {
              user_id = userVisitAction.user_id
            }

            //并不是每一个动作都有搜索关键字，只有搜索行为才有搜索关键字，其余用户行为该字段为空
            val search_keyword = userVisitAction.search_keyword
            //并不是每一个动作都有点击商品类别id，只有点击行为才有，其余的动作该字段为空
            val click_category_id = userVisitAction.click_category_id

            //将非空的search_keyword做追加字符串处理
            if (StringUtils.isNotEmpty(search_keyword)) {
              //一个用户在一次session中可能会多次搜索同一个关键字
              if (!searchKeyWordsBuffer.toString.contains(search_keyword)) {
                searchKeyWordsBuffer.append(search_keyword + ",")
              }
            }

            //将非空的click_category_id做字符串拼接处理
            if (click_category_id != null && click_category_id != -1L) {
              if (!clickCategoryIdsBuffer.toString.contains(click_category_id.toString)) {
                clickCategoryIdsBuffer.append(click_category_id + ",")
              }
            }

            //计算session开始和结束的时间，即算出最开始的动作时间和最后的动作时间的差值
            val action_time = DateUtils.parseTime(userVisitAction.action_time)
            if (startTime == null) {
              startTime = action_time
            }
            if (endTime == null) {
              endTime = action_time
            }
            //遇到不满足的时间，做重新赋值处理
            if (action_time.before(startTime)) {
              startTime = action_time
            }
            if (action_time.after(endTime)) {
              endTime = action_time
            }

            //计算一个session的访问步长
            stepLength += 1
          }
        }

        //将逗号进行去除处理
        val searchKeyWords = StringUtils.trimComma(searchKeyWordsBuffer.toString)
        val clickCategoryIds = StringUtils.trimComma(clickCategoryIdsBuffer.toString)

        //计算session的访问时长(单位是秒)
        val visitLength = (endTime.getTime - startTime.getTime) / 1000

        //聚合数据，即字符串的拼接
        val partAggrInfo =
          Constants.FIELD_SESSION_ID + "=" + session_id + "|" +
            Constants.FIELD_SEARCH_KEYWORDS + "=" + searchKeyWords + "|" +
            Constants.FIELD_CLICK_CATEGORY_IDS + "=" + clickCategoryIds + "|" +
            Constants.FIELD_VISIT_LENGTH + "=" + visitLength + "|" +
            Constants.FIELD_STEP_LENGTH + "=" + stepLength + "|" +
            Constants.FIELD_START_TIME + "=" + DateUtils.formatTime(startTime)

        //(user_id,sessionid=session_id|searchKeywords=searchKeyWords|clickCategoryIds=clickCategoryIds|visitLength=visitLength|stepLength=stepLength|startTime=startTime)
        (user_id, partAggrInfo)
      }
    }

    //查询所有的用户数据，并组成（user_id，row）的格式
    import spark.implicits._
    val sql = "select * from db_userbehaviors.user_info"
    val user_infoRDD = spark.sql(sql).as[UserInfo].rdd
    val userid2InfoRDD = user_infoRDD.map(
      line => {
        (line.user_id, line)
      })

    //将session粒度聚合数据，与用户信息进行join操作
    val userid2FullInfoRDD = user_id2PartAggrInfoRDD.join(userid2InfoRDD)

    //将join后的数据进行字符串的拼接，返回(session_id,fullAggrInfo)格式的数据
    val session_id2FullAggrInfoRDD = userid2FullInfoRDD.map {
      case (uid, (partAggrInfo, userInfo)) => {
        //从拼接的字符串中提取字段
        val session_id = StringUtils.getFieldFromConcatString(partAggrInfo, "\\|", Constants.FIELD_SESSION_ID)

        val fullAggrInfo =
          partAggrInfo + "|" +
            Constants.FIELD_AGE + "=" + userInfo.age + "|" +
            Constants.FIELD_PROFESSIONAL + "=" + userInfo.professional + "|" +
            Constants.FIELD_CITY + "=" + userInfo.city + "|" +
            Constants.FIELD_SEX + "=" + userInfo.sex

        (session_id, fullAggrInfo)
      }
    }
    session_id2FullAggrInfoRDD
  }

  /**
   * 以session_id为key值，对value值进行过滤，并聚合统计
   *
   * @param RDD         传入进来的session_id2AggrInfoRDD
   * @param taskParam   过滤条件中JSON对象
   * @param accumulator 累加器
   * @return
   */
  def filterSessionAndAggrStat(RDD: RDD[(String, String)],
                               taskParam: JSONObject,
                               accumulator: AccumulatorV2[String, mutable.HashMap[String, Int]]) = {
    //获取查询任务中的配置
    val startAge = ParamUtils.getParam(taskParam, Constants.PARAM_START_AGE)
    val endAge = ParamUtils.getParam(taskParam, Constants.PARAM_END_AGE)
    val professionals = ParamUtils.getParam(taskParam, Constants.PARAM_PROFESSIONALS)
    val cites = ParamUtils.getParam(taskParam, Constants.PARAM_CITIES)
    val sex = ParamUtils.getParam(taskParam, Constants.PARAM_SEX)
    val keywords = ParamUtils.getParam(taskParam, Constants.PARAM_KEYWORDS)
    val categoryIds = ParamUtils.getParam(taskParam, Constants.PARAM_CATEGORY_IDS)

    //如果获取的值为null值，就进行空串拼接
    var _parameter =
      (if (startAge != null) {
        Constants.PARAM_START_AGE + "=" + startAge + "|"
      } else {
        ""
      }) +
        (if (endAge != null) {
          Constants.PARAM_END_AGE + "=" + endAge + "|"
        } else {
          ""
        }) +
        (if (professionals != null) {
          Constants.PARAM_PROFESSIONALS + "=" + professionals + "|"
        } else {
          ""
        }) +
        (if (cites != null) {
          Constants.PARAM_CITIES + "=" + cites + "|"
        } else {
          ""
        }) +
        (if (sex != null) {
          Constants.PARAM_SEX + "=" + sex + "|"
        } else {
          ""
        }) +
        (if (keywords != null) {
          Constants.PARAM_KEYWORDS + "=" + keywords + "|"
        } else {
          ""
        }) +
        (if (categoryIds != null) {
          Constants.PARAM_CATEGORY_IDS + "=" + categoryIds + "|"
        } else {
          ""
        })

    //将结尾的|线给删除
    if (_parameter.endsWith("\\|")) {
      _parameter = _parameter.substring(0, _parameter.length - 1)
    }
    val parameter = _parameter

    //根据筛选参数进行过滤
    val filteredSession_id2AggrInfoRDD = RDD.filter {
      case (session_id, aggrInfo) => {

        var success = true

        //按照筛选条件过滤：按年龄范围进行过滤
        if (!ValidUtils.between(aggrInfo, Constants.FIELD_AGE, parameter, Constants.PARAM_START_AGE, Constants.PARAM_END_AGE)) {
          success = false
        }

        //按照职业范围进行过滤（professionals）
        if (!ValidUtils.in(aggrInfo, Constants.FIELD_PROFESSIONAL, parameter, Constants.PARAM_PROFESSIONALS)) {
          success = false
        }

        //按男女性别进行过滤
        if (!ValidUtils.in(aggrInfo, Constants.FIELD_SEX, parameter, Constants.PARAM_SEX)) {
          success = false
        }

        //按照搜索词进行过滤，在搜索关键词字符串中与过滤条件中一个搜索条件满足就过滤通过
        if (!ValidUtils.in(aggrInfo, Constants.FIELD_SEARCH_KEYWORDS, parameter, Constants.PARAM_KEYWORDS)) {
          success = false
        }

        //按照点击品类id进行过滤
        if (!ValidUtils.in(aggrInfo, Constants.FIELD_CLICK_CATEGORY_IDS, parameter, Constants.PARAM_CATEGORY_IDS)) {
          success = false
        }

        //如果符合任务搜索需求
        if (success) {
          accumulator.add(Constants.SESSION_COUNT)

          //计算session的访问时长和访问步长的范围，并进行相应的累加
          val visitLength = StringUtils.getFieldFromConcatString(aggrInfo, "\\|", Constants.FIELD_VISIT_LENGTH).toLong
          val stepLength = StringUtils.getFieldFromConcatString(aggrInfo, "\\|", Constants.FIELD_STEP_LENGTH).toLong
          this.calculateVisitLength(visitLength, accumulator)
          this.calculateStepLength(stepLength, accumulator)
        }


        success
      }
    }

    filteredSession_id2AggrInfoRDD
  }

  /**
   * 计算访问时长范围
   *
   * @param visitLength 一次session中访问的时长
   * @param accumulator 累加器
   */
  def calculateVisitLength(visitLength: Long, accumulator: AccumulatorV2[String, mutable.HashMap[String, Int]]) {
    if (visitLength >= 1 && visitLength <= 3) {
      accumulator.add(Constants.TIME_PERIOD_1s_3s)
    } else if (visitLength >= 4 && visitLength <= 6) {
      accumulator.add(Constants.TIME_PERIOD_4s_6s)
    } else if (visitLength >= 7 && visitLength <= 9) {
      accumulator.add(Constants.TIME_PERIOD_7s_9s)
    } else if (visitLength >= 10 && visitLength <= 30) {
      accumulator.add(Constants.TIME_PERIOD_10s_30s)
    } else if (visitLength > 30 && visitLength <= 60) {
      accumulator.add(Constants.TIME_PERIOD_30s_60s)
    } else if (visitLength > 60 && visitLength <= 180) {
      accumulator.add(Constants.TIME_PERIOD_1m_3m)
    } else if (visitLength > 180 && visitLength <= 600) {
      accumulator.add(Constants.TIME_PERIOD_3m_10m)
    } else if (visitLength > 600 && visitLength <= 1800) {
      accumulator.add(Constants.TIME_PERIOD_10m_30m)
    } else if (visitLength > 1800) {
      accumulator.add(Constants.TIME_PERIOD_30m)
    }
  }

  /**
   * 计算出session的访问时长和访问步长的范围，并进行相应的累加
   *
   * @param stepLength  一次session中访问的步长
   * @param accumulator 累加器
   */
  def calculateStepLength(stepLength: Long, accumulator: AccumulatorV2[String, mutable.HashMap[String, Int]]) {
    if (stepLength >= 1 && stepLength <= 3) {
      accumulator.add(Constants.STEP_PERIOD_1_3)
    } else if (stepLength >= 4 && stepLength <= 6) {
      accumulator.add(Constants.STEP_PERIOD_4_6)
    } else if (stepLength >= 7 && stepLength <= 9) {
      accumulator.add(Constants.STEP_PERIOD_7_9)
    } else if (stepLength >= 10 && stepLength <= 30) {
      accumulator.add(Constants.STEP_PERIOD_10_30)
    } else if (stepLength > 30 && stepLength <= 60) {
      accumulator.add(Constants.STEP_PERIOD_30_60)
    } else if (stepLength > 60) {
      accumulator.add(Constants.STEP_PERIOD_60)
    }
  }

  /**
   * 获取通过筛选条件的session的访问明细数据的RDD
   * 用户表中记录了用户详细的个人信息，包括年龄、职业、城市、性别等，在实际的业务场景中，
   * 我们可能会在一段时间关注某一个群体的用户的行为，比如在某一段时间关注北京的白领们的购物行为，
   * 那么我们就可以通过联立用户表，让我们的统计数据中具有用户属性，然后根据用户属性对统计信息进行过滤，
   * 将不属于我们所关注的用户群体的用户所产生的行为数据过滤掉，这样就可以实现对指定人群的精准分析。
   *
   * @param session_id2aggrInfoRDD 以session_id为key，基础明细信息为value的RDD
   * @param session_id2actionRDD   以session_id为key，用户行为对象为value的RDD
   * @return
   */
  def getSession_id2detailRDD(
                               session_id2aggrInfoRDD: RDD[(String, String)],
                               session_id2actionRDD: RDD[(String, UserVisitAction)]) = {
    session_id2aggrInfoRDD.join(session_id2actionRDD).map(item => {
      (item._1, item._2._2)
    })
  }

  /**
   * 计算各个session范围的占比，把结果写入到mysql中
   *
   * @param value
   * @param taskUUID
   */
  def calculateAndPersisAggrStat(value: mutable.HashMap[String, Int], taskUUID: String) = {
    //从累加器中获取值
    val session_count = value(Constants.SESSION_COUNT).toDouble

    val visit_length_1s_3s = value.getOrElse(Constants.TIME_PERIOD_1s_3s, 0)
    val visit_length_4s_6s = value.getOrElse(Constants.TIME_PERIOD_4s_6s, 0)
    val visit_length_7s_9s = value.getOrElse(Constants.TIME_PERIOD_7s_9s, 0)
    val visit_length_10s_30s = value.getOrElse(Constants.TIME_PERIOD_10s_30s, 0)
    val visit_length_30s_60s = value.getOrElse(Constants.TIME_PERIOD_30s_60s, 0)
    val visit_length_1m_3m = value.getOrElse(Constants.TIME_PERIOD_1m_3m, 0)
    val visit_length_3m_10m = value.getOrElse(Constants.TIME_PERIOD_3m_10m, 0)
    val visit_length_10m_30m = value.getOrElse(Constants.TIME_PERIOD_10m_30m, 0)
    val visit_length_30m = value.getOrElse(Constants.TIME_PERIOD_30m, 0)

    val step_length_1_3 = value.getOrElse(Constants.STEP_PERIOD_1_3, 0)
    val step_length_4_6 = value.getOrElse(Constants.STEP_PERIOD_4_6, 0)
    val step_length_7_9 = value.getOrElse(Constants.STEP_PERIOD_7_9, 0)
    val step_length_10_30 = value.getOrElse(Constants.STEP_PERIOD_10_30, 0)
    val step_length_30_60 = value.getOrElse(Constants.STEP_PERIOD_30_60, 0)
    val step_length_60 = value.getOrElse(Constants.STEP_PERIOD_60, 0)

    //计算各个访问时长和访问步长的范围
    val visit_length_1s_3s_ratio = NumberUtils.formatDouble(visit_length_1s_3s / session_count, 2)
    val visit_length_4s_6s_ratio = NumberUtils.formatDouble(visit_length_4s_6s / session_count, 2)
    val visit_length_7s_9s_ratio = NumberUtils.formatDouble(visit_length_7s_9s / session_count, 2)
    val visit_length_10s_30s_ratio = NumberUtils.formatDouble(visit_length_10s_30s / session_count, 2)
    val visit_length_30s_60s_ratio = NumberUtils.formatDouble(visit_length_30s_60s / session_count, 2)
    val visit_length_1m_3m_ratio = NumberUtils.formatDouble(visit_length_1m_3m / session_count, 2)
    val visit_length_3m_10m_ratio = NumberUtils.formatDouble(visit_length_3m_10m / session_count, 2)
    val visit_length_10m_30m_ratio = NumberUtils.formatDouble(visit_length_10m_30m / session_count, 2)
    val visit_length_30m_ratio = NumberUtils.formatDouble(visit_length_30m / session_count, 2)

    val step_length_1_3_ratio = NumberUtils.formatDouble(step_length_1_3 / session_count, 2)
    val step_length_4_6_ratio = NumberUtils.formatDouble(step_length_4_6 / session_count, 2)
    val step_length_7_9_ratio = NumberUtils.formatDouble(step_length_7_9 / session_count, 2)
    val step_length_10_30_ratio = NumberUtils.formatDouble(step_length_10_30 / session_count, 2)
    val step_length_30_60_ratio = NumberUtils.formatDouble(step_length_30_60 / session_count, 2)
    val step_length_60_ratio = NumberUtils.formatDouble(step_length_60 / session_count, 2)

    //封装统计结果
    val sessionAggrStat = SessionAggrStat(taskUUID,
      session_count.toInt,
      visit_length_1s_3s_ratio,
      visit_length_4s_6s_ratio,
      visit_length_7s_9s_ratio,
      visit_length_10s_30s_ratio,
      visit_length_30s_60s_ratio,
      visit_length_1m_3m_ratio,
      visit_length_3m_10m_ratio,
      visit_length_10m_30m_ratio,
      visit_length_30m_ratio,
      step_length_1_3_ratio,
      step_length_4_6_ratio,
      step_length_7_9_ratio,
      step_length_10_30_ratio,
      step_length_30_60_ratio,
      step_length_60_ratio
    )

    import spark.implicits._
    val sessionAggrStatRDD = sc.makeRDD(Array(sessionAggrStat))
    sessionAggrStatRDD
      .toDF()
      .write
      .format("jdbc")
      .option("url", ConfigurationManager.config.getString(Constants.JDBC_URL))
      .option("dbtable", ConfigurationManager.config.getString(Constants.JDBC_TABLE_SESSIONAGGRSTATRDD))
      .option("user", ConfigurationManager.config.getString(Constants.JDBC_USER))
      .option("password", ConfigurationManager.config.getString(Constants.JDBC_PASSWORD))
      .mode(SaveMode.Append)
      .save()
  }
}
