package analysis.advertising.bean

/**
 * 各省top3热门广告
 *
 * @param date
 * @param province
 * @param adid
 * @param clickCount
 */
case class AdProvinceTop3(date: String,
                          province: String,
                          adid: Long,
                          clickCount: Long
                         )
