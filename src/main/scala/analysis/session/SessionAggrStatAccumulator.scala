package analysis.session

import org.apache.spark.util.AccumulatorV2

import scala.collection.mutable

/**
 * 自定义累加器
 */
class SessionAggrStatAccumulator extends AccumulatorV2[String, mutable.HashMap[String, Int]] {

  //保存所有的聚合数据
  private val aggrStatMap: mutable.HashMap[String, Int] = mutable.HashMap[String, Int]()

  //判断是否是初始值
  override def isZero: Boolean = {
    aggrStatMap.isEmpty
  }

  override def copy(): AccumulatorV2[String, mutable.HashMap[String, Int]] = {
    val newACC = new SessionAggrStatAccumulator()

    aggrStatMap.synchronized {
      newACC.aggrStatMap ++= this.aggrStatMap
    }
    newACC
  }

  override def reset(): Unit = {
    aggrStatMap.clear()
  }

  override def add(v: String): Unit = {
    if (!aggrStatMap.contains(v)) {
      aggrStatMap += (v -> 0)
    } else {
      aggrStatMap.update(v, aggrStatMap(v) + 1)
    }
  }

  override def merge(other: AccumulatorV2[String, mutable.HashMap[String, Int]]): Unit = {
    other match {
      case acc: SessionAggrStatAccumulator => {
        //  /:就是lefFold操作
        (this.aggrStatMap /: acc.value){
          case (map,(k,v)) =>{
            map += (k -> (v +  map.getOrElse(k,0)))
          }
        }
      }
    }
  }

  override def value: mutable.HashMap[String, Int] = {
    this.aggrStatMap
  }
}
