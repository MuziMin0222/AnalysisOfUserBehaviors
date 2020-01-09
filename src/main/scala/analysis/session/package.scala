package analysis

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

package object session {
  val conf: SparkConf = new SparkConf().setAppName("SessionAnalysis").setMaster("local[*]")
  val spark: SparkSession = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()

  val sc: SparkContext = spark.sparkContext
}
