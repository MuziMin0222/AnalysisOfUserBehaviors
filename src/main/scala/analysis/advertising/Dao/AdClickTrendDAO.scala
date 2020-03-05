package analysis.advertising.Dao

import java.sql.ResultSet

import analysis.advertising.bean.AdClickTrend
import commons.pool.{CreateMysqlPool, MySqlProxy, QueryCallback}

import scala.collection.mutable.ArrayBuffer

/**
 * 广告点击趋势DAO实现类
 */
object AdClickTrendDAO {
  def updateBatch(adclickTrends: Array[AdClickTrend]) = {
    //获取对象池单例对象
    val mySqlPool = CreateMysqlPool()
    //从对象池中提取对象
    val client: MySqlProxy = mySqlPool.borrowObject()

    //区分开哪些是要插入的，哪些是要更新的
    val updateAdClickTrends: ArrayBuffer[AdClickTrend] = ArrayBuffer[AdClickTrend]()
    val insertAdClickTrends: ArrayBuffer[AdClickTrend] = ArrayBuffer[AdClickTrend]()

    val selectSQL = "select count(*) " +
      "from commerce.ad_click_trend " +
      "where date=? " +
      "and hour=? " +
      "and minute=? " +
      "and adid=?"

    for (adClickTrend <- adclickTrends) {
      //通过查询结果判断当前项是待插入还是待更新
      val params = Array[Any](adClickTrend.date, adClickTrend.hour, adClickTrend.minute, adClickTrend.adid)
      client.executeQuery(selectSQL, params, new QueryCallback {
        override def process(rs: ResultSet): Unit = {
          if (rs.next() && rs.getInt(1) > 0) {
            updateAdClickTrends += adClickTrend
          } else {
            insertAdClickTrends += adClickTrend
          }
        }
      })
    }

    //执行批量更新操作，此处的update是覆盖
    val updateSQL = "update commerce.ad_click_trend set clickCount=? "+
    "where date=? "+
    "and hour=? "+
    "and minute=? " +
    "and adid=?"

    val updateParamsList = ArrayBuffer[Array[Any]]()

    for (adClickTrend <- adclickTrends){
      updateParamsList += Array[Any](adClickTrend.clickCount,adClickTrend.date,adClickTrend.hour,adClickTrend.minute,adClickTrend.adid)
    }

    client.executeBatch(updateSQL,updateParamsList.toArray)

    //执行批量插入操作
    val insertSQL = "insert into commerce.ad_click_trend values(?,?,?,?,?)"

    val insertParamsList = ArrayBuffer[Array[Any]]()

    for (adClickTrend <-  insertAdClickTrends){
      insertParamsList += Array(adClickTrend.date,adClickTrend.hour,adClickTrend.minute,adClickTrend.adid,adClickTrend.clickCount)
    }

    client.executeBatch(insertSQL,insertParamsList.toArray)

    //将对象返回给对象池
    mySqlPool.returnObject(client)
  }
}
