package analysis.advertising.bean

/**
 * 广告点击趋势
 *
 * @param date
 * @param hour
 * @param minute
 * @param adid
 * @param clickCount
 */
case class AdClickTrend(date: String,
                        hour: String,
                        minute: String,
                        adid: Long,
                        clickCount: Long)
