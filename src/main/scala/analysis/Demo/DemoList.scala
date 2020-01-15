package analysis.Demo

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object DemoList {
  def main(args: Array[String]): Unit = {
    val ints: ListBuffer[Int] = mutable.ListBuffer(1,2,3)

    println(ints)

    ints :+ 4
    println(ints)

    ints += 5
    println(ints)
  }
}
