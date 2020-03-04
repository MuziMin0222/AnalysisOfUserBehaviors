package analysis.advertising.Dao

import java.sql.ResultSet

import analysis.advertising.bean.AdBlacklist
import commons.pool.{CreateMysqlPool, QueryCallback}

import scala.collection.mutable.ArrayBuffer

/**
 * 用户黑名单DAO类
 */
object AdBlacklistDAO {
  /**
   * 批量插入广告黑名单用户
   *
   * @param adBlackLists
   */
  def insetBatch(adBlackLists: Array[AdBlacklist]) = {
    //批量插入
    val sql = "insert into ad_blacklist values(?)"

    val paramsList = new ArrayBuffer[Array[Any]]()

    //向paramsList添加userId
    for (adBlackList <- adBlackLists) {
      val params: Array[Any] = Array(adBlackList.userid)
      paramsList += params
    }

    //获取对象池单例对象
    val mySqlPool = CreateMysqlPool()

    //从对象池中提取对象
    val client = mySqlPool.borrowObject()

    //执行批量插入操作
    client.executeBatch(sql, paramsList.toArray)

    //使用完成后将对象返回给对象池
    mySqlPool.returnObject(client)
  }

  /**
   * 查询所有的广告黑名单用户
   *
   * @return
   */
  def findAll() = {
    //将黑名单中的所有数据查询出来
    val sql = "select * from ad_blacklist"

    val adBlacklists = new ArrayBuffer[AdBlacklist]()

    //获取对象池单例对象
    val mySqlPool = CreateMysqlPool()

    //从对象池中提取对象
    val client = mySqlPool.borrowObject()

    //执行SQL查询并且通过处理函数将所有的userId加入到array中
    client.executeQuery(sql, null, new QueryCallback {
      override def process(rs: ResultSet): Unit = {
        while (rs.next()) {
          val userid = rs.getInt(1).toLong
          adBlacklists += AdBlacklist(userid)
        }
      }
    })

    //使用完成后将对象返回给对象池
    mySqlPool.returnObject(client)
    adBlacklists.toArray
  }
}
