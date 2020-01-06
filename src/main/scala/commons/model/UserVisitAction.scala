package commons.model

/**
 * 用户访问动作表
 * @param date                        用户点击行为的日期
 * @param user_id                     用户的ID
 * @param session_id                  Session的ID
 * @param page_id                     某个页面的ID
 * @param action_time                 点击行为的时间点
 * @param search_keyword              用户搜索的关键词
 * @param click_category_id           某一个商品品类的ID
 * @param click_product_id            某一个商品的ID
 * @param order_category_ids          一次订单中所有品类的ID集合
 * @param order_product_ids           一次订单中所有商品的ID集合
 * @param pay_category_ids            一次支付中所有品类的ID集合
 * @param pay_product_ids             一次支付中所有商品的ID集合
 * @param city_id                     城市ID
 */
case class UserVisitAction(date: String,
                           user_id: Long,
                           session_id: String,
                           page_id: Long,
                           action_time: String,
                           search_keyword: String,
                           click_category_id: Long,
                           click_product_id: Long,
                           order_category_ids: String,
                           order_product_ids: String,
                           pay_category_ids: String,
                           pay_product_ids: String,
                           city_id: Long
                          )