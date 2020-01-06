package commons.pool

import java.sql.Connection

import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.{BasePooledObjectFactory, PooledObject}

/**
 * 创建自定义工厂类，继承BasePooledObjectFactory工厂类，负责对象的创建、包装和销毁
 *
 * @param jdbcUrl      url
 * @param jdbcUser     用户
 * @param jdbcPassword 密码
 * @param client       连接对象
 */
class PooledMySqlClientFactory(jdbcUrl: String, jdbcUser: String, jdbcPassword: String, client: Option[Connection] = None)
  extends BasePooledObjectFactory[MySqlProxy] with Serializable {

  // 用于池来创建对象
  override def create(): MySqlProxy = MySqlProxy(jdbcUrl, jdbcUser, jdbcPassword, client)

  // 用于池来包装对象
  override def wrap(obj: MySqlProxy): PooledObject[MySqlProxy] = new DefaultPooledObject(obj)

  // 用于池来销毁对象
  override def destroyObject(p: PooledObject[MySqlProxy]): Unit = {
    p.getObject.shutDown()
    super.destroyObject(p)
  }

}