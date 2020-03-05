package analysis.advertising.Function

import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.DStream
import analysis._
import analysis.advertising.Dao.AdProvinceTop3DAO
import analysis.advertising.bean.AdProvinceTop3
import org.apache.spark.sql.Row

import scala.collection.mutable.ArrayBuffer

object AdClickRealTimeStatFunction3 {

  /**
   * 需求九：每天每个省份Top3热门广告
   * 根据需求八中统计的各省各城市累计广告点击量，创建SparkSQL临时表，通过SQL查询的形式获取各省的Top3热门广告。
   *
   * @param key2ProvinceCityCountDStream 需求八得到的DStream
   */
  def provinceTop3Ad(key2ProvinceCityCountDStream: DStream[(String, Long)]) = {
    //key2ProvinceCityCountDStream:RDD[key,Count] 其中key为date_province_city_adid
    //将粒度，newKey为：date_province_adid
    val key2ProvinceCountDStream: DStream[(String, Long)] = key2ProvinceCityCountDStream.map {
      case (key, count) => {
        val keySplit: Array[String] = key.split("_")
        val date: String = keySplit(0)
        val province: String = keySplit(1)
        val adid: String = keySplit(3)

        val newKey = date + "_" + province + "_" + adid
        (newKey, count)
      }
    }

    val key2ProvinceAggrCountDStream: DStream[(String, Long)] = key2ProvinceCountDStream.reduceByKey(_ + _)

    val top3DStream: DStream[Row] = key2ProvinceAggrCountDStream.transform {
      rdd => {
        //rdd:RDD[(key,count)]   key:date_province_adid
        val basicDataRDD: RDD[(String, String, Long, Long)] = rdd.map {
          case (key, count) => {
            val keySplit: Array[String] = key.split("_")
            val date: String = keySplit(0)
            val province: String = keySplit(1)
            val adid: Long = keySplit(2).toLong

            (date, province, adid, count)
          }
        }

        import spark.implicits._
        basicDataRDD.toDF("date", "province", "adid", "count").createOrReplaceTempView("tmp_basic_info")

        val sql = "select date,province,adid,count from " +
          "(select date,province,adid,count," +
          "row_number() over (partition by date order by count desc) rank " +
          "from tmp_basic_info) t " +
          "where rank <= 3"

        spark.sql(sql).rdd
      }
    }

    top3DStream.foreachRDD {
      //rdd : RDD[row]
      rdd => {
        rdd.foreachPartition {
          //items : row
          items => {
            val top3Array = new ArrayBuffer[AdProvinceTop3]()

            for (item <- items) {
              val date: String = item.getAs[String]("date")
              val province: String = item.getAs[String]("province")
              val adid = item.getAs[Long]("adid")
              val count = item.getAs[Long]("count")

              top3Array += AdProvinceTop3(date,province,adid,count)
            }

            AdProvinceTop3DAO.updateBatch(top3Array.toArray)
          }
        }
      }
    }
  }
}
