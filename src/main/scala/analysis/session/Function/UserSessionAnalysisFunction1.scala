package analysis.session.Function

import analysis.session.accumulator.SessionAggrStatAccumulator
import analysis._

object UserSessionAnalysisFunction1 {

  /**
   * 需求一：各个范围Session步长、访问时长占比统计
   *
   * 统计出符合筛选条件的session中，访问时长在
   * 1s~3s、4s~6s、7s~9s、10s~30s、30s~60s、1m~3m、3m~10m、10m~30m、30m，
   * 访问步长在1_3、4_6、…以上各个范围内的各种session的占比。
   *
   * 访问时长：session的最早时间和最晚时间只差
   * 访问步长：session中action的个数
   *
   * 并将session_idDetailRDD作为结果返回
   */
  def Demand1() = {

    //从db_userbehaviors.user_visit_action读出数据,查询指定日期范围内的行为数据
    //UserVisitAction(2020-01-07,91,1235481e78ec49f986f00651709450b2,8,2020-01-07 1:39:32,太古商场,-1,-1,null,null,null,null,4)
    val actionRDD = Demand1Function.getActionRDDByDateRange(taskParam)

    //将用户行为信息转换成k-V结构
    //(1235481e78ec49f986f00651709450b2,UserVisitAction(2020-01-07,91,1235481e78ec49f986f00651709450b2,8,2020-01-07 1:39:32,太古商场,-1,-1,null,null,null,null,4))
    val SessionId2ActionRDD = actionRDD.map(obj => {
      (obj.session_id, obj)
    })

    //将RDD中的对象给去除，转化为数据类型的RDD
    //(fc5216ce783a4cebb983405e5c7b9915,sessionid=fc5216ce783a4cebb983405e5c7b9915|searchKeywords=国贸大厦,太古商场,温泉,蛋糕,重庆小面,火锅|clickCategoryIds=76,73,80,36,31|visitLength=3108|stepLength=25|startTime=2020-01-07 19:06:08|age=0|professional=professional78|city=city93|sex=female)
    val session_id2AggrInfoRDD = Demand1Function.aggregateBySession(SessionId2ActionRDD)

    //设置自定义累加器，实现所有数据的统计功能，注意累加器是懒加载的
    val sessionAggrStatAccumulator = new SessionAggrStatAccumulator
    sc.register(sessionAggrStatAccumulator)

    //根据查询任务的配置，过滤用户的行为数据，在过滤的过程中进行累加
    //(b5368124e22d45af85c1c5a38ab9be4c,sessionid=b5368124e22d45af85c1c5a38ab9be4c|searchKeywords=新辣道鱼火锅,温泉,太古商场,呷哺呷哺,日本料理,重庆小面|clickCategoryIds=83,23,43,63,56,46,27,41,21,57,93,51|visitLength=3491|stepLength=53|startTime=2020-01-08 12:00:33|age=31|professional=professional90|city=city98|sex=female)
    val filterSession_id2AggrInfoRDD = Demand1Function.filterSessionAndAggrStat(session_id2AggrInfoRDD, taskParam, sessionAggrStatAccumulator)

    filterSession_id2AggrInfoRDD.collect()
    sessionAggrStatAccumulator.value.foreach(println(_))

    //过滤后的明细信息与用户行为源数据进行聚合操作（session_id，userActions）,即是符合过滤条件的完整数据
    //(c283e1a1ed6d449ebfbc6e13bc9e35f8,UserVisitAction(2020-01-08,91,c283e1a1ed6d449ebfbc6e13bc9e35f8,3,2020-01-08 3:57:24,null,-1,-1,60,11,null,null,3))
    val session_idDetailRDD = Demand1Function.getSession_id2detailRDD(filterSession_id2AggrInfoRDD, SessionId2ActionRDD)

    //统计各个范围的session占比，并写入到mysql中
    Demand1Function.calculateAndPersisAggrStat(sessionAggrStatAccumulator.value, taskUUID)

    sc.setCheckpointDir("hdfs://bd1:9000/AnalysisUserBehaviors/filterSession_id2AggrInfoRDD")
    filterSession_id2AggrInfoRDD.checkpoint()
    sc.setCheckpointDir("hdfs://bd1:9000/AnalysisUserBehaviors/session_idDetailRDD")
    session_idDetailRDD.checkpoint()

    (filterSession_id2AggrInfoRDD,session_idDetailRDD,SessionId2ActionRDD)
  }
}
