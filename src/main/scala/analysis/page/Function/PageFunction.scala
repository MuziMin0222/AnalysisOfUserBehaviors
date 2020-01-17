package analysis.page.Function

import commons.model.UserVisitAction
import commons.utils.{DateUtils, NumberUtils, ParamUtils}
import org.apache.spark.rdd.RDD
import analysis._
import commons.constant.Constants
import org.apache.spark.broadcast.Broadcast

import scala.collection.mutable

object PageFunction {

  /**
   * 页面切片生成与匹配算法，一开始我们只有userAction信息，通过UserAction按照时间进行排序，然后提取PageId，在进行连接
   *
   * @param session2actionsRDD 用户行为信息
   * @return
   */
  def generateAndMatchPageSplit(session2actionsRDD: RDD[(String, Iterable[UserVisitAction])]) = {
    //对目标Pageflow进行解析
    val targetPageFlow: String = ParamUtils.getParam(taskParam, Constants.PARAM_TARGET_PAGE_FLOW)

    //将字符串转换成List[String]
    val targetPage: List[String] = targetPageFlow.split(",").toList

    val targetPagePairs: List[String] = targetPage.slice(0, targetPage.length - 1).zip(targetPage.tail).map {
      item => {
        item._1 + "_" + item._2
      }
    }

    //将结果转换成广播变量
    val targetPagePairsBC: Broadcast[List[String]] = sc.broadcast(targetPagePairs)

    /**
     * 将乱序的session的访问行为按照时间进行排序
     */
    session2actionsRDD.flatMap {
      case (session_id, userVisitActions) => {
        //按时间顺序排序
        val sortedUVAS = userVisitActions.toList.sortWith(
          (uva1, uva2) => {
            DateUtils.parseTime(uva1.action_time).getTime < DateUtils.parseTime(uva2.action_time).getTime
          }
        )

        //提取所有的UserAction中的pageId信息
        val soredPages: List[AnyVal] = sortedUVAS.map(item => {
          if (item.page_id != null) {
            item.page_id
          }
        })

        //按照已经排好的顺序对pageId信息进行整合，生成所有的页面切片
        val sessionPagePairs: List[String] = soredPages.slice(0, soredPages.length - 1).zip(soredPages.tail).map(item => item._1 + "_" + item._2)

        //如果当前session的PageFlow有一个切片与targetPageFlow中任一切片重合，那么就保留下来
        sessionPagePairs.filter(targetPagePairsBC.value.contains(_)).map((_, 1))
      }
    }
  }

  /**
   * 计算首页PV的数量
   *
   * @param session_id2actionsRDD 用户动作信息
   */
  def getStartPagePV(session_id2actionsRDD: RDD[(String, Iterable[UserVisitAction])]) = {
    //获取配置文件中的targetPageFlow
    val targetPageFlow: String = ParamUtils.getParam(taskParam, Constants.PARAM_TARGET_PAGE_FLOW)

    //获取起始页面ID
    val startPageId: Long = targetPageFlow.split(",")(0).toLong

    val startPageRDD: RDD[Long] = session_id2actionsRDD.flatMap {
      case (session_id, userVisitActions) => {
        userVisitActions.filter(_.page_id == startPageId).map(_.page_id)
      }
    }

    startPageRDD.count()
  }

  /**
   * 计算页面切片转化率
   * @param pageSplitPvMap  页面切片PV
   * @param startPagePV      起始页面PV
   * @return
   */
  def computePageSplitConvertRate(pageSplitPvMap: collection.Map[String, Long],startPagePV: Long) = {
    val convertRateMap = new mutable.HashMap[String,Double]()

    val targetPageFlow: String = ParamUtils.getParam(taskParam,Constants.PARAM_TARGET_PAGE_FLOW)

    val targetPages: List[String] = targetPageFlow.split(",").toList

    val targetPagePairs = targetPages.slice(0, targetPages.length-1).zip(targetPages.tail).map(item => item._1 + "_" + item._2)

    println("targetPagePairs:" + targetPagePairs)

    //存储最新一次的页面PV数量
    var lastPageSplitPv: Double = startPagePV.toDouble

    //通过for循环，获取目标页面流中各个页面切片
    for (targetPage <- targetPagePairs){
      //先获取pageSplitPVMap中记录的当前targetPage的数量
      val targetPageSplitPv: Double = pageSplitPvMap.get(targetPage).get.toDouble

      //用当前targetPage的数量除以上一次lastPageSplit的数量，得到转化率
      val converRate: Double = NumberUtils.formatDouble(targetPageSplitPv / lastPageSplitPv,2)

      //对targetPage和转化率进行存储
      convertRateMap.put(targetPage,converRate)

      //将本次的targetPage作为下一次的lastPageSplitPv
      lastPageSplitPv = targetPageSplitPv
    }

    convertRateMap
  }

}