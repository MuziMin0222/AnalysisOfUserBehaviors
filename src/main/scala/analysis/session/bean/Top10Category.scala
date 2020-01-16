package analysis.session.bean

/**
 * 品类Top10表
 * @param taskid
 * @param categoryid
 * @param clickCount
 * @param orderCount
 * @param payCount
 */
case class Top10Category(taskid:String,
                         categoryid:Long,
                         clickCount:Long,
                         orderCount:Long,
                         payCount:Long)