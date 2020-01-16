package analysis.session.Function

/**
 * 自定义二次排序的类
 */
case class CategorySoryKey(clickCount: Long, orderCount: Long, payCount: Long) extends Ordered[CategorySoryKey] {

  /**
   * 比较的方法
   * x < 0  this < that
   * x == 0  this == that
   * x > 0 this > that
   *
   * @param that 传进来要比较的参数
   * @return
   */
  override def compare(that: CategorySoryKey): Int = {
    if (this.clickCount - that.clickCount != 0) {
      return (this.clickCount - that.clickCount).toInt
    } else if (this.orderCount - that.orderCount != 0) {
      return (this.orderCount - that.orderCount).toInt
    } else if (this.payCount - that.payCount != 0) {
      return (this.payCount - that.payCount).toInt
    }
    0
  }
}
