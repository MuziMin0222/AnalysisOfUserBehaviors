package analysis.advertising.APP

import analysis._
import analysis.advertising.Function.{AdClickRealTimeStatFunction, AdClickRealTimeStatFunction2, AdClickRealTimeStatFunction3, AdClickRealTimeStatFunction4}
import commons.conf.ConfigurationManager
import commons.constant.Constants
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object AdClickRealTimeStat {
  def main(args: Array[String]): Unit = {
    val ssc = new StreamingContext(sc, Seconds(5))

    //设置实时计算的检查点
    ssc.checkpoint("hdfs://bd1:9000/AnalysisUserBehaviors/RealTimeData")

    val kafka_brokers = ConfigurationManager.config.getString(Constants.KAFKA_BROKER_LIST)
    val kafka_topics = ConfigurationManager.config.getString(Constants.KAFKA_TOPICS)

    val kafkaParam = Map(
      "bootstrap.servers" -> kafka_brokers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "group1",
      // auto.offset.reset
      // latest: 先去Zookeeper获取offset，如果有，直接使用，如果没有，从最新的数据开始消费；
      // earlist: 先去Zookeeper获取offset，如果有，直接使用，如果没有，从最开始的数据开始消费
      // none: 先去Zookeeper获取offset，如果有，直接使用，如果没有，直接报错
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false:java.lang.Boolean)
    )

    // adRealTimeDStream: DStream[RDD RDD RDD ...]  RDD[message]  message: key value
    val adRealTimeDStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Array(kafka_topics), kafkaParam)
    )

    //过滤数据
    val adRealTimeFilterDStream: DStream[String] = AdClickRealTimeStatFunction.FilterDStream(adRealTimeDStream)

    adRealTimeFilterDStream.foreachRDD(rdd => rdd.foreach(println(_)))

    //需求7：广告点击黑名单实时统计
    AdClickRealTimeStatFunction.generateBlackList(adRealTimeFilterDStream)

    //需求8：各省各城市广告点击量实时统计
    val key2ProvinceCityCountDStream: DStream[(String, Long)] =
      AdClickRealTimeStatFunction2.provinceCityClickStat(adRealTimeFilterDStream)

    //需求9：每天每个省份Top3热门广告
    AdClickRealTimeStatFunction3.provinceTop3Ad(key2ProvinceCityCountDStream)

    //需求10:最近一小时广告点击量实时统计
    AdClickRealTimeStatFunction4.getRecentHourClickCount(adRealTimeFilterDStream)

    ssc.start()
    ssc.awaitTermination()
  }
}
