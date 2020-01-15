package analysis.session.bean

/**
* Session随机抽取表
*
* @param taskid             当前计算批次的ID
* @param sessionid          抽取的Session的ID
* @param startTime          Session的开始时间
* @param searchKeywords     Session的查询字段
* @param clickCategoryIds   Session点击的类别id集合
*/
case class SessionRandomExtract(taskid:String,
                                sessionid:String,
                                startTime:String,
                                searchKeywords:String,
                                clickCategoryIds:String)
