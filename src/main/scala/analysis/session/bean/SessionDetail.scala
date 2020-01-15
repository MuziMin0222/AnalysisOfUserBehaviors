package analysis.session.bean

/**
 * Session随机抽取详细表
 *
 * @param taskid            当前计算批次的ID
 * @param userid            用户的ID
 * @param sessionid         Session的ID
 * @param pageid            某个页面的ID
 * @param actionTime        点击行为的时间点
 * @param searchKeyword     用户搜索的关键词
 * @param clickCategoryId   某一个商品品类的ID
 * @param clickProductId    某一个商品的ID
 * @param orderCategoryIds  一次订单中所有品类的ID集合
 * @param orderProductIds   一次订单中所有商品的ID集合
 * @param payCategoryIds    一次支付中所有品类的ID集合
 * @param payProductIds     一次支付中所有商品的ID集合
 **/
case class SessionDetail(taskid:String,
                         userid:Long,
                         sessionid:String,
                         pageid:Long,
                         actionTime:String,
                         searchKeyword:String,
                         clickCategoryId:Long,
                         clickProductId:Long,
                         orderCategoryIds:String,
                         orderProductIds:String,
                         payCategoryIds:String,
                         payProductIds:String)