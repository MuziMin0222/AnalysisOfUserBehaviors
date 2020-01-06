package demo

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

object Demo {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("demo")
    val sc = new SparkContext(conf)

    sc.makeRDD(Array(1, 2, 3, 4, 5)).collect().foreach(println)

    val spark = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()
    import spark.implicits._

    spark.sql("create database user")
    spark.sql("show databases").show()

    val rdd = sc.makeRDD(Array(1,2,3,4,5))
    val df = rdd.toDF("id")
    df.createTempView("user")
    spark.sql("select * from user").show()
  }
}
