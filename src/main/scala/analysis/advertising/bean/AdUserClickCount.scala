package analysis.advertising.bean

/**
 * 广告实时统计
 *
 * @param date
 * @param userid
 * @param adid
 * @param clickCount
 */
case class AdUserClickCount(date: String,
                            userid: Long,
                            adid: Long,
                            clickCount: Long)
