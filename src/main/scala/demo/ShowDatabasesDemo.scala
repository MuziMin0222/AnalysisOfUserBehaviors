package demo

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object ShowDatabasesDemo {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("ShowDatabases").setMaster("local[*]")
    val spark = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()

    spark.sql("show databases").show()

    spark.sql("show tables in db_userbehaviors").show()

    spark.sql("select * from db_userbehaviors.product_info limit 10").show()
  }
}
