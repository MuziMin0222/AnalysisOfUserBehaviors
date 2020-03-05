import java.util.UUID

import commons.conf.ConfigurationManager
import commons.constant.Constants
import net.sf.json.JSONObject
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

package object analysis {
  val conf: SparkConf = new SparkConf().setAppName("SessionAnalysis").setMaster("local[*]")
  val spark: SparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()

  val sc: SparkContext = spark.sparkContext

  //获取统计任务参数，即过滤参数
  val jsonStr = ConfigurationManager.config.getString(Constants.TASK_PARAMS)
  val taskParam = JSONObject.fromObject(jsonStr)

  //任务执行的ID，用户唯一标示运行后的结果，用在MySQL数据库中作为主键使用
  val taskUUID = UUID.randomUUID().toString
}
