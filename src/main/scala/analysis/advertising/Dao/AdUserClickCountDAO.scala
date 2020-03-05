package analysis.advertising.Dao

import java.sql.ResultSet

import analysis.advertising.bean.AdUserClickCount
import commons.pool.{CreateMysqlPool, QueryCallback}

import scala.collection.mutable.ArrayBuffer

/**
 * 用户广告点击量DAO实现类
 *
 */
object AdUserClickCountDAO {

  def updateBatch(adUserClickCounts: Array[AdUserClickCount]) {
    // 获取对象池单例对象
    val mySqlPool = CreateMysqlPool()
    // 从对象池中提取对象
    val client = mySqlPool.borrowObject()

    // 首先对用户广告点击量进行分类，分成待插入的和待更新的
    val insertAdUserClickCounts = ArrayBuffer[AdUserClickCount]()
    val updateAdUserClickCounts = ArrayBuffer[AdUserClickCount]()

    val selectSQL = "SELECT count(*) FROM commerce.ad_user_click_count WHERE date=? AND userid=? AND adid=? "

    for (adUserClickCount <- adUserClickCounts) {

      val selectParams: Array[Any] = Array(adUserClickCount.date, adUserClickCount.userid, adUserClickCount.adid)
      // 根据传入的用户点击次数统计数据从已有的ad_user_click_count中进行查询
      client.executeQuery(selectSQL, selectParams, new QueryCallback {
        override def process(rs: ResultSet): Unit = {
          // 如果能查询到并且点击次数大于0，则认为是待更新项
          if (rs.next() && rs.getInt(1) > 0) {
            updateAdUserClickCounts += adUserClickCount
          } else {
            insertAdUserClickCounts += adUserClickCount
          }
        }
      })
    }

    // 执行批量插入
    val insertSQL = "INSERT INTO commerce.ad_user_click_count VALUES(?,?,?,?)"
    val insertParamsList: ArrayBuffer[Array[Any]] = ArrayBuffer[Array[Any]]()

    // 将待插入项全部加入到参数列表中
    for (adUserClickCount <- insertAdUserClickCounts) {
      insertParamsList += Array[Any](adUserClickCount.date, adUserClickCount.userid, adUserClickCount.adid, adUserClickCount.clickCount)
    }

    // 执行批量插入
    client.executeBatch(insertSQL, insertParamsList.toArray)

    // 执行批量更新
    // clickCount=clickCount + ：此处的UPDATE是进行累加
    val updateSQL = "UPDATE commerce.ad_user_click_count SET clickCount=clickCount + ? WHERE date=? AND userid=? AND adid=?"
    val updateParamsList: ArrayBuffer[Array[Any]] = ArrayBuffer[Array[Any]]()

    // 将待更新项全部加入到参数列表中
    for (adUserClickCount <- updateAdUserClickCounts) {
      updateParamsList += Array[Any](adUserClickCount.clickCount, adUserClickCount.date, adUserClickCount.userid, adUserClickCount.adid)
    }

    // 执行批量更新
    client.executeBatch(updateSQL, updateParamsList.toArray)

    // 使用完成后将对象返回给对象池
    mySqlPool.returnObject(client)
  }

  /**
   * 根据多个key查询用户广告点击量
   *
   * @param date   日期
   * @param userid 用户id
   * @param adid   广告id
   * @return
   */
  def findClickCountByMultiKey(date: String, userid: Long, adid: Long): Int = {
    // 获取对象池单例对象
    val mySqlPool = CreateMysqlPool()
    // 从对象池中提取对象
    val client = mySqlPool.borrowObject()

    val sql = "SELECT clickCount FROM commerce.ad_user_click_count " +
      "WHERE date=? " +
      "AND userid=? " +
      "AND adid=?"

    var clickCount = 0
    val params = Array[Any](date, userid, adid)

    // 根据多个条件查询指定用户的点击量，将查询结果累加到clickCount中
    client.executeQuery(sql, params, new QueryCallback {
      override def process(rs: ResultSet): Unit = {
        if (rs.next()) {
          clickCount = rs.getInt(1)
        }
      }
    })
    // 使用完成后将对象返回给对象池
    mySqlPool.returnObject(client)
    clickCount
  }
}