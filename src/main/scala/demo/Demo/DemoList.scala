package demo.Demo

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object DemoList {
  def main(args: Array[String]): Unit = {
    val ints: ListBuffer[Int] = mutable.ListBuffer(1,2,3,4,5,6,7,8,9)

    println(ints)

    ints :+ 4
    println(ints)

    ints += 5
    println(ints)

    println(ints.slice(1, 3))

    println(ints.tail)

    println(ints.zip(ints.tail))
  }
}
