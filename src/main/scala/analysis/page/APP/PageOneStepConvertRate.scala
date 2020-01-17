package analysis.page.APP

import analysis._
import analysis.page.Function.PageFunction
import analysis.page.bean.PageSplitConvertRate
import commons.conf.ConfigurationManager
import commons.constant.Constants
import commons.model.UserVisitAction
import commons.utils.ParamUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SaveMode

import scala.collection.mutable

/**
 * 需求五：页面单跳转化率模块的spark作业
 *
 * 我们需要去分析每一次的页面访问流程，也就是用户从进入网站到离开网站这个流程中所访问的页面顺序，
 * 也就是一个session中的页面访问顺序。
 *
 * 假如一个session的页面访问顺序为1,2,3,4,5，那么他访问的页面切片就是1_2，2_3，3_4，4_5，
 * 如果得到所有session的切面切片m_n，就可以计算每一种页面切片的个数，即count(m_n)，
 * 就可以计算每一个页面单跳转化率。
 */
object PageOneStepConvertRate {
  def main(args: Array[String]): Unit = {
    val startDate: String = ParamUtils.getParam(taskParam,Constants.PARAM_START_DATE)
    val endDate: String = ParamUtils.getParam(taskParam,Constants.PARAM_END_DATE)

    //获取指定日期范围内的用户访问数据
    import spark.implicits._
    val sql = "select * from db_userbehaviors.user_visit_action where date >= '" + startDate + "' and date <= '" + endDate + "'"
    val actionRDD = spark.sql(sql).as[UserVisitAction].rdd

    //将数据转为k-v结构
    val session_id2actionRDD: RDD[(String, UserVisitAction)] = actionRDD.map(item => {
      (item.session_id, item)
    })

    //对<session_id,访问行为>RDD，做一次groupByKey操作，生成页面切片
    val session_id2actionsRDD: RDD[(String, Iterable[UserVisitAction])] = session_id2actionRDD.groupByKey()

    val pageSplitRDD: RDD[(String, Int)] = PageFunction.generateAndMatchPageSplit(session_id2actionsRDD)

    //统计每个跳转切片的总个数
    val pageSplitPvMap: collection.Map[String, Long] = pageSplitRDD.countByKey()
    println(pageSplitPvMap)

    //首先计算首页PV的数量
    val startPagePV: Long = PageFunction.getStartPagePV(session_id2actionsRDD)

    //计算目标页面流的各个页面切片的转化率
    val convertRateMap: mutable.HashMap[String, Double] = PageFunction.computePageSplitConvertRate(pageSplitPvMap,startPagePV)

    //持久化页面切片转化率
    val converRate: String = convertRateMap.map(item => {
      item._1 + "=" + item._2
    }).mkString("|")


    val pageSplitConverRateRDD: RDD[PageSplitConvertRate] = sc.makeRDD(Array(PageSplitConvertRate(taskUUID,converRate)))

    pageSplitConverRateRDD.toDF().write
      .format("jdbc")
      .option("url",ConfigurationManager.config.getString(Constants.JDBC_URL))
      .option("dbtable",ConfigurationManager.config.getString(Constants.JDBC_TABLE_PAGE_SPLIT_CONVERT_RATE))
      .option("user",ConfigurationManager.config.getString(Constants.JDBC_USER))
      .option("password",ConfigurationManager.config.getString(Constants.JDBC_PASSWORD))
      .mode(SaveMode.Append)
      .save()
  }
}
