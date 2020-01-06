package commons.pool

import commons.conf.ConfigurationManager
import commons.constant.Constants
import org.apache.commons.pool2.impl.{GenericObjectPool, GenericObjectPoolConfig}

/**
 * 创建MySQL池工具类
 */
object CreateMysqlPool {

  //加载JDBC驱动，只需要执行一次
  Class.forName("com.mysql.cj.jdbc.Driver")

  //在org.apache.commons.pool2.impl中预设了三个可以直接使用的对象池：
  // GenericObjectPool、GenericKeyedObjectPool和SoftReferenceObjectPool
  //GenericObjectPool的特点是可以设置对象池中的对象特征，包括LIFO方式、最大空闲数、最小空闲数、是否有效性检查等等
  private var genericObjectPool: GenericObjectPool[MySqlProxy] = null

  //伴生对象通过apply完成对象的创建
  def apply(): GenericObjectPool[MySqlProxy] = {
    // 单例模式
    if (this.genericObjectPool == null) {
      this.synchronized {
        // 获取MySQL配置参数
        val jdbcUrl = ConfigurationManager.config.getString(Constants.JDBC_URL)
        val jdbcUser = ConfigurationManager.config.getString(Constants.JDBC_USER)
        val jdbcPassword = ConfigurationManager.config.getString(Constants.JDBC_PASSWORD)
        val size = ConfigurationManager.config.getInt(Constants.JDBC_DATASOURCE_SIZE)

        val pooledFactory = new PooledMySqlClientFactory(jdbcUrl, jdbcUser, jdbcPassword)
        val poolConfig = {
          // 创建标准对象池配置类的实例
          val config = new GenericObjectPoolConfig
          // 设置配置对象参数,设置最大对象数
          config.setMaxTotal(size)
          // 设置最大空闲对象数
          config.setMaxIdle(size)
          config
        }
        // 对象池的创建需要工厂类和配置类
        // 返回一个GenericObjectPool对象池
        this.genericObjectPool = new GenericObjectPool[MySqlProxy](pooledFactory, poolConfig)
      }
    }
    genericObjectPool
  }
}