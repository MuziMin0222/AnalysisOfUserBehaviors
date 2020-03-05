package analysis.advertising.Function

import java.util.Date

import analysis.advertising.Dao.{AdBlacklistDAO, AdUserClickCountDAO}
import analysis.advertising.bean.{AdBlacklist, AdUserClickCount}
import commons.utils.DateUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.dstream.{DStream, InputDStream}

import scala.collection.mutable.ArrayBuffer

object AdClickRealTimeStatFunction {

  /**
   * /从kafka中读出的数据根据黑名单进行过滤
   *
   * @param adRealTimeDStream kafka中读取的数据
   * @return
   */
  def FilterDStream(adRealTimeDStream: InputDStream[ConsumerRecord[String, String]]) = {
    // 取出了DSream里面每一条数据的value值
    // adReadTimeValueDStream: Dstram[RDD  RDD  RDD ...]   RDD[String]
    // String:  timestamp province city userid adid
    val adReadTimeValueDStream: DStream[String] = adRealTimeDStream.map(item => item.value())

    /*
      刚刚接收到原始的用户点击行为日志之后，根据mysql的动态黑名单，进行实时的黑名单过滤（黑名单用户的点击行为，直接过来吧掉，不要了）
      使用transform算子：将DStream中的每个batch RDD进行处理，转化为任意的RDD，功能强大
     */
    val adRealTimeFilterDStream: DStream[String] = adReadTimeValueDStream.transform {
      logRDD => {

        // blackListArray: Array[AdBlacklist]     AdBlacklist: userId
        val blackListArray: Array[AdBlacklist] = AdBlacklistDAO.findAll()

        // userIdArray: Array[Long]  [userId1, userId2, ...]
        val userIdArray: Array[Long] = blackListArray.map(item => item.userid)

        logRDD.filter {
          // log : timestamp province city userid adid
          log => {
            val logSplit: Array[String] = log.split(" ")
            val userId: Long = logSplit(3).toLong
            !userIdArray.contains(userId)
          }
        }
      }
    }

    adRealTimeFilterDStream
  }

  /**
   * 从Kafka获取实时数据，对每个用户的点击次数进行累加并写入MySQL，
   * 当一天之内一个用户对一个广告的点击次数超过100次时，将用户加入黑名单中。
   *
   * @param adRealTimeFilterDStream kafka过滤后的数据
   */
  def generateBlackList(adRealTimeFilterDStream: DStream[String]) = {
    //adRealTimeFilterDStream   String -> :timestamp province city  userid  adid
    val key2NumDStream: DStream[(String, Long)] = adRealTimeFilterDStream.map {
      log => {
        val logSplit: Array[String] = log.split(" ")
        val timeStamp: Long = logSplit(0).toLong
        //yyyy-mm-dd
        val dateKey: String = DateUtils.formatDateKey(new Date(timeStamp))
        val userId: Long = logSplit(3).toLong
        val adId: Long = logSplit(4).toLong

        val key = dateKey + "_" + userId + "_" + adId

        (key, 1L)
      }
    }

    val key2CountDStream: DStream[(String, Long)] = key2NumDStream.reduceByKey(_ + _)

    //根据每一个RDD里面的数据，更新用户点击次数表
    key2CountDStream.foreachRDD {
      rdd => {
        rdd.foreachPartition {
          items => {
            val clickCountArray = new ArrayBuffer[AdUserClickCount]()

            for ((key, count) <- items) {
              val keySplit: Array[String] = key.split("_")
              val date: String = keySplit(0)
              val userId: Long = keySplit(1).toLong
              val adId: Long = keySplit(2).toLong

              clickCountArray += AdUserClickCount(date, userId, adId, count)
            }

            AdUserClickCountDAO.updateBatch(clickCountArray.toArray)
          }
        }
      }
    }

    //key2CountDStream：DStream[RDD[(key,count)]]
    val key2BlackListDStram: DStream[(String, Long)] = key2CountDStream.filter {
      case (key, count) => {
        val keySplits: Array[String] = key.split("_")
        val date: String = keySplits(0)
        val userId = keySplits(1).toLong
        val adId: Long = keySplits(2).toLong

        val clickCount = AdUserClickCountDAO.findClickCountByMultiKey(date, userId, adId)

        if (clickCount > 100) {
          true
        } else {
          false
        }
      }
    }

    //提取userId
    val userIdDStream: DStream[Long] = key2BlackListDStram.map {
      case (key, count) => key.split("_")(1).toLong
    }.transform(rdd => rdd.distinct())

    userIdDStream.foreachRDD {
      rdd =>
        rdd.foreachPartition {
          items => {
            val userIdArray = new ArrayBuffer[AdBlacklist]()

            for (userId <- items) {
              userIdArray += AdBlacklist(userId)
            }

            AdBlacklistDAO.insetBatch(userIdArray.toArray)
          }
        }
    }
  }
}
