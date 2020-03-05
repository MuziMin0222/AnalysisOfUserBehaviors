package analysis.advertising.Function

import java.util.Date

import analysis.advertising.Dao.AdStatDAO
import analysis.advertising.bean.AdStat
import commons.utils.DateUtils
import org.apache.spark.streaming.dstream.DStream

import scala.collection.mutable.ArrayBuffer

object AdClickRealTimeStatFunction2 {

  /**
   * 需求八：各省各城市广告点击量实时统计
   * 通过SparkStreaming的updateStateByKey算子累计计算各省的广告点击量。
   *
   * @param adRealTimeFilterDStream kafka过滤后的数据
   */
  def provinceCityClickStat(adRealTimeFilterDStream: DStream[String]) = {
    // adRealTimeFilterDStream: DStream[RDD[String]]    String -> log : timestamp province city userid adid
    // key2ProvinceCityDStream: DStream[RDD[key, 1L]]
    val key2ProvinceCityDStream = adRealTimeFilterDStream.map{
      case log =>
        val logSplit = log.split(" ")
        val timeStamp = logSplit(0).toLong
        // dateKey : yy-mm-dd
        val dateKey = DateUtils.formatDateKey(new Date(timeStamp))
        val province = logSplit(1)
        val city = logSplit(2)
        val adid = logSplit(4)

        val key = dateKey + "_" + province + "_" + city + "_" + adid
        (key, 1L)
    }

    val key2StateDStream = key2ProvinceCityDStream.updateStateByKey[Long]{
      (values:Seq[Long], state:Option[Long]) =>
        var newValue = 0L
        if(state.isDefined)
          newValue = state.get
        for(value <- values){
          newValue += value
        }
        Some(newValue)
    }

    key2StateDStream.foreachRDD{
      rdd => rdd.foreachPartition{
        items =>
          val adStatArray = new ArrayBuffer[AdStat]()
          // key: date province city adid
          for((key, count) <- items){
            val keySplit = key.split("_")
            val date = keySplit(0)
            val province = keySplit(1)
            val city = keySplit(2)
            val adid = keySplit(3).toLong

            adStatArray += AdStat(date, province, city, adid, count)
          }
          AdStatDAO.updateBatch(adStatArray.toArray)
      }
    }

    key2StateDStream
  }
}
