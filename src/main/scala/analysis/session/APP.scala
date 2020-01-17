package analysis.session

import analysis.session.Function.{UserSessionAnalysisFunction1, UserSessionAnalysisFunction2, UserSessionAnalysisFunction3, UserSessionAnalysisFunction4}

/**
 * 大需求一：用户访问session统计
 */
object APP {
  def main(args: Array[String]): Unit = {
    //小需求一：各个范围Session步长、访问时长占比统计
    //UserSessionAnalysisFunction1.Demand1()

    //小需求二：session的随机抽取
    //UserSessionAnalysisFunction2.Demand2()

    //小需求三：top10热门品类统计
//    UserSessionAnalysisFunction3.demand3()

    //小需求四：
    UserSessionAnalysisFunction4.deamnd4()
  }
}
