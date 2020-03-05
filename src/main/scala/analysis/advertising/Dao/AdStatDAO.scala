package analysis.advertising.Dao

import java.sql.ResultSet

import analysis.advertising.bean.AdStat
import commons.pool.{CreateMysqlPool, QueryCallback}

import scala.collection.mutable.ArrayBuffer

/**
 * 广告实时统计DAO实现类
 */
object AdStatDAO {
  def updateBatch(adStats: Array[AdStat]) = {
    //获取对象池单例对象
    val mySqlPool = CreateMysqlPool()
    //从对象池中提取对象
    val client = mySqlPool.borrowObject()

    //区分开哪些是要插入的，哪些是要更新的
    val insertAdStats = ArrayBuffer[AdStat]()
    val updateAdStats = ArrayBuffer[AdStat]()

    val selectSQL = "select count(*) " +
      "from commerce.ad_stat " +
      "where date=? " +
      "and province=? " +
      "and city=? " +
      "and adid=?"

    for (adStat <- adStats) {
      val params = Array[Any](adStat.date, adStat.province, adStat.city, adStat.adid)

      //通过查询结果判断当前项是待插入还是待更新
      client.executeQuery(selectSQL, params, new QueryCallback {
        override def process(rs: ResultSet): Unit = {
          if (rs.next() && rs.getInt(1) > 0) {
            updateAdStats += adStat
          } else {
            insertAdStats += adStat
          }
        }
      })
    }

    //对于需要插入的数据，执行批量插入操作
    val insertSQL = "insert into commerce.ad_stat values(?,?,?,?,?)"

    val insertParamsList = ArrayBuffer[Array[Any]]()

    for (adStat <- insertAdStats) {
      insertParamsList += Array[Any](adStat.date, adStat.province, adStat.city, adStat.adid, adStat.clickCount)
    }

    client.executeBatch(insertSQL, insertParamsList.toArray)

    //对于需要更新的数据，执行批量更新操作
    val updateSQL = "update commerce.ad_stat set clickCount=? " +
      "where date=? " +
      "and province=? " +
      "and city=? " +
      "and adid=?"

    val updateParamsList: ArrayBuffer[Array[Any]] = ArrayBuffer[Array[Any]]()

    for (adStat <- updateAdStats){
      updateParamsList += Array[Any](adStat.clickCount,adStat.date,adStat.province,adStat.city,adStat.adid)
    }

    client.executeBatch(updateSQL,updateParamsList.toArray)

    //使用完成后将对象返回给对象池
    mySqlPool.returnObject(client)
  }
}
