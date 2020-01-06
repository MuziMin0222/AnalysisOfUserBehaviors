package commons.pool

import java.sql.ResultSet

trait QueryCallback {
  // 创建用于处理MySQL查询结果的类的抽象接口
  def process(rs: ResultSet)
}