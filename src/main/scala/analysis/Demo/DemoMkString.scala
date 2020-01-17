package analysis.Demo

import scala.collection.mutable

object DemoMkString{
  def main(args: Array[String]): Unit = {
    val map = new mutable.HashMap[String,Int]()

    map.put("lhm",21)
    map.put("lihuangmin",22)

    println(map.map(item => {
      item._1 + "=" + item._2
    }).mkString("|"))
  }
}
