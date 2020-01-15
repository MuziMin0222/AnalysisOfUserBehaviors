package analysis.Demo

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object DemoMap {
  def main(args: Array[String]): Unit = {
    val dateHourCountMap = mutable.HashMap[String, mutable.HashMap[String, Long]](("lihuangmin", mutable.HashMap[String, Long]("dd" -> 123, "lihuangmin" -> 456)))

    println(dateHourCountMap.size)
    println(dateHourCountMap.values)

    val stringToStringToListBuffer: mutable.HashMap[String, mutable.HashMap[String, ListBuffer[Int]]] = mutable.HashMap[String, mutable.HashMap[String, ListBuffer[Int]]](
      ("lihuangmin", mutable.HashMap[String, ListBuffer[Int]](("lhm", ListBuffer(12, 21, 32))))
    )

    stringToStringToListBuffer.values

    var sum = 0

    stringToStringToListBuffer.values.foreach(item => {
      val values: Iterable[ListBuffer[Int]] = item.values
      values.foreach(item => {
        for (elem <- item) {
          sum += elem
        }
      })
    })

    var count = 0
    println(
      stringToStringToListBuffer.values.foreach(item => {
        item.reduce((x, y) => {
          count = x._2.sum + y._2.sum
          ("   ", ListBuffer(count))
        })
      }))

    println(sum)
  }
}
