package analysis.product.bean

/**
 *
 * @param taskid
 * @param area
 * @param areaLevel
 * @param productid
 * @param cityInfos
 * @param clickCount
 * @param productName
 * @param productStatus
 */
case class AreaTop3Product(taskid:String,
                           area:String,
                           areaLevel:String,
                           productid:Long,
                           cityInfos:String,
                           clickCount:Long,
                           productName:String,
                           productStatus:String)