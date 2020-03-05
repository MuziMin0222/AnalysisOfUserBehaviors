package analysis.advertising.bean

/**
 * 广告实时统计
 *
 * @param date
 * @param province
 * @param city
 * @param adid
 * @param clickCount
 */
case class AdStat(date: String,
                  province: String,
                  city: String,
                  adid: Long,
                  clickCount: Long)
