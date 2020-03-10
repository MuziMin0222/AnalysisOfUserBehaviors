package demo.Demo

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object DemoJoin {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("demo")
    val sc = new SparkContext(conf)

    val rdd1= sc.makeRDD(Array((1,"lhm")))
    val rdd2 = sc.makeRDD(Array((1,"lihuangmin"),(2,"luzhen")))

    rdd1.join(rdd2).foreach(println(_))
    rdd1.rightOuterJoin(rdd2).foreach(println(_))
  }
}
