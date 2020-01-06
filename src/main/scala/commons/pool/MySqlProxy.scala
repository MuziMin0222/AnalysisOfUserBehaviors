package commons.pool

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

//将MysqlProxy实例类视为对象，MySQLProxy实例的创建使用对象池进行维护
case class MySqlProxy(jdbcUrl: String, jdbcUser: String, jdbcPassword: String, client: Option[Connection] = None) {

  //获取客户端连接对象
  private val mysqlClient: Connection = client.getOrElse {
    DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword)
  }

  /**
   * 执行增删改SQL的语句
   *
   * @param sql SQL语句
   * @param pss 预写入对象PreparedStatement数组
   */
  def executeUpdate(sql: String, pss: Array[Any]) = {
    var rtn = 0
    var ps: PreparedStatement = null

    try {
      //关闭自动提交
      mysqlClient.setAutoCommit(false)

      //根据传入的sql语句创建prepareStatement
      ps = mysqlClient.prepareStatement(sql)

      //为prepareStatement中的每个参数填写数值
      if (pss != null && pss.length > 0) {
        for (i <- 0 until pss.length) {
          ps.setObject(i + 1, pss(i))
        }
      }

      //执行增删改操作
      rtn = ps.executeUpdate()

      //手动提交
      mysqlClient.commit()
    } catch {
      case e: Exception => e.printStackTrace()
    }

    rtn
  }

  /**
   * 执行select 查询语句
   *
   * @param sql SQL语句
   * @param pss 预写入对象PreparedStatement数组
   */
  def executeQuery(sql: String, pss: Array[Any], queryCallback: QueryCallback) {
    var rs: ResultSet = null
    var ps: PreparedStatement = null

    try {
      //根据传入的sql语句创建prepareStatement
      ps = mysqlClient.prepareStatement(sql)

      //为prepareStatement中的每个参数填写数值
      if (pss != null && pss.length > 0) {
        for (i <- 0 until pss.length) {
          ps.setObject(i + 1, pss(i))
        }
      }

      //执行查询操作
      rs = ps.executeQuery()

      //处理查询后的结果
      queryCallback.process(rs)
    } catch {
      case exception: Exception => exception.printStackTrace()
    }
  }

  /**
   * 批量执行SQL语句
   *
   * @param sql     SQL语句
   * @param pssList 预写入对象PreparedStatement数组的数组
   */
  def executeBatch(sql: String, pssList: Array[Array[Any]]) = {
    var rtn: Array[Int] = null
    var ps: PreparedStatement = null

    try {
      //关闭自动提交
      mysqlClient.setAutoCommit(false)
      ps = mysqlClient.prepareStatement(sql)

      //为prepareStatement中的每个参数填写数值
      if (pssList != null && pssList.length > 0) {
        for (pss <- pssList) {
          for (i <- 0 until pss.length) {
            ps.setObject(i + 1, pss(i))
          }
          ps.addBatch()
        }
      }

      //执行批量的SQL语句
      rtn = ps.executeBatch()

      //手动提交
      mysqlClient.commit()
    } catch {
      case exception: Exception => exception.printStackTrace()
    }

    rtn
  }

  //关闭mysql客户端
  def shutDown() = {
    mysqlClient.close()
  }
}