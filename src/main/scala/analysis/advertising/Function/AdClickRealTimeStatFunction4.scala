package analysis.advertising.Function

import java.util.Date

import analysis.advertising.Dao.AdClickTrendDAO
import analysis.advertising.bean.AdClickTrend
import commons.utils.DateUtils
import org.apache.spark.streaming.Minutes
import org.apache.spark.streaming.dstream.DStream

import scala.collection.mutable.ArrayBuffer

object AdClickRealTimeStatFunction4 {

  /**
   * 需求十：最近一小时广告点击量实时统计
   * 通过Spark Streaming的窗口操作(reduceByKeyAndWindow)实现统计一个小时内每个广告每分钟的点击量。
   *
   * @param adRealTimeFilterDStream
   */
  def getRecentHourClickCount(adRealTimeFilterDStream: DStream[String]) = {
    val key2TimeMinuteDStream: DStream[(String, Long)] = adRealTimeFilterDStream.map {
      //log : timeStamp province city userid  adid
      log => {
        val logSplit: Array[String] = log.split(" ")

        val timestamp: Long = logSplit(0).toLong
        //timeMinute : yyyyMMddHHmm
        val timeMinute: String = DateUtils.formatTimeMinute(new Date(timestamp))
        val adid: Long = logSplit(4).toLong

        val key = timeMinute + "_" + adid

        (key, 1L)
      }
    }

    //一小时为一个窗口，一分钟计算一次
    val key2WindowDStream: DStream[(String, Long)] =
      key2TimeMinuteDStream.reduceByKeyAndWindow((a:Long,b:Long) => (a+b),Minutes(60),Minutes(1))

    key2WindowDStream.foreachRDD{
      rdd => {
        rdd.foreachPartition{
          //(key,count)
          items => {
            val trendArray = new ArrayBuffer[AdClickTrend]()

            for ((key,count) <- items) {
              val keySplit: Array[String] = key.split("_")
              //yyyyMMddHHmm
              val timeMinute: String = keySplit(0)
              val date: String = timeMinute.substring(0,8)
              val hour: String = timeMinute.substring(8,10)
              val minute: String = timeMinute.substring(10,12)
              val adid: Long = keySplit(1).toLong

              trendArray += AdClickTrend(date,hour,minute,adid,count)
            }

            AdClickTrendDAO.updateBatch(trendArray.toArray)
          }
        }
      }
    }
  }
}
