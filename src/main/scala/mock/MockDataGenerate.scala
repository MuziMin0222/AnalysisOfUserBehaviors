package mock

import java.util.UUID

import commons.model.{ProductInfo, UserInfo, UserVisitAction}
import commons.utils.{DateUtils, StringUtils}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
 * 模拟离线数据
 * date：当前的日期
 * age：0-59
 * professionals: professional[0 - 59]
 * cities: 0 - 9
 * sex: 0 - 1
 * keywords: ("火锅", "蛋糕", "重庆辣子鸡", "重庆小面", "呷哺呷哺", "新辣道鱼火锅", "国贸大厦", "太古商场", "日本料理", "温泉")
 * categoryIds: 0 - 99
 * ProductId: 0 - 99
 */
object MockDataGenerate {

  /**
   * 模拟用户行为信息
   * @return 将用户行为信息组成一行放到数组中
   */
  private def mockUserVisitActionData()={
    val searchKeywords = Array("火锅", "蛋糕", "重庆辣子鸡", "重庆小面", "呷哺呷哺", "新辣道鱼火锅", "国贸大厦", "太古商场", "日本料理", "温泉")

    //yyyy-MM-dd
    val date = DateUtils.getTodayDate()

    //四个行为：点击，搜索，下单，支付
    val actions = Array("search","click","order","pay")

    val random = new Random()
    val rows = ArrayBuffer[UserVisitAction]()

    //一共一百个用户，可重复
    for (i <- 0 to 100) {
      val user_id = random.nextInt(100)

      //每一个用户产生10个session
      for (j <- 0 to 10){
        //不可变的，全局的，独一无二的128bit长度的标识符，用于标识一个session，体现一次会话产生的sessionId是独一无二的
        val session_id = UUID.randomUUID().toString().replace("-","")

        // 在yyyy-MM-dd后面添加一个随机的小时时间（0-23）
        val baseActionTime = date + " " + random.nextInt(23)

        //// 每个(userid + sessionid)生成0-100条用户访问数据
        for (k <- 0 to random.nextInt(100)){
          //访问页面的ID
          val page_id = random.nextInt(100)

          // 在yyyy-MM-dd HH后面添加一个随机的分钟时间和秒时间
          val actionTime = baseActionTime+
            ":" + StringUtils.fulfuill(String.valueOf(random.nextInt(59))) +
            ":" + StringUtils.fulfuill(String.valueOf(random.nextInt(59)))

          var searchKeyword: String = null
          var clickCategoryId: Long = -1L
          var clickProductId: Long = -1L
          var orderCategoryIds: String = null
          var orderProductIds: String = null
          var payCategoryIds: String = null
          var payProductIds: String = null
          val cityid = random.nextInt(10).toLong

          // 随机确定用户在当前session中的行为
          val action = actions(random.nextInt(4))

          // 根据随机产生的用户行为action决定对应字段的值
          action match {
            case "search" =>
              searchKeyword = searchKeywords(random.nextInt(10))
            case "click" =>
              clickCategoryId = random.nextInt(100).toLong
              clickProductId = String.valueOf(random.nextInt(100)).toLong
            case "order" =>
              orderCategoryIds = random.nextInt(100).toString
              orderProductIds = random.nextInt(100).toString
            case "pay" =>
              payCategoryIds = random.nextInt(100).toString
              payProductIds = random.nextInt(100).toString
          }

          //将一行的数据进行整合
          rows += UserVisitAction(
            date, user_id, session_id,
            page_id, actionTime, searchKeyword,
            clickCategoryId, clickProductId,
            orderCategoryIds, orderProductIds,
            payCategoryIds, payProductIds, cityid)
        }
      }
    }
    rows.toArray
  }

  /**
   * 模拟用户信息表
   */
  private def mockUserInfo() = {
    val rows = ArrayBuffer[UserInfo]()
    val sexes = Array("male", "female")
    val random = new Random()

    // 随机产生100个用户的个人信息
    for (i<- 1 to 100) {
      val user_id = i
      val username = "user" + i
      val name = "name" + i
      val age = random.nextInt(60)
      val professional = "professional" + random.nextInt(100)
      val city = "city" + random.nextInt(100)
      val sex = sexes(random.nextInt(2))
      rows += UserInfo(user_id,username,name,age,professional,city,sex)
    }
    rows.toArray
  }

  /**
   * 模拟产品数据表
   */
  private def mockProductInfo() ={
    val rows = ArrayBuffer[ProductInfo]()
    val random = new Random()
    val productStatus = Array(0, 1)

    //随机产生100个随机产品信息
    for (i <- 0 to 100) {
      val product_id = i;
      val productName = "product" + i
      val extendInfo = "{\"product_status\":" + productStatus(random.nextInt(2)) + "}"

      rows += ProductInfo(product_id,productName,extendInfo)
    }
    rows.toArray
  }

  /**
   * 将DataFrame插入到hive表中
   */
  private def insertHive(spark:SparkSession,tableName:String,DF:DataFrame)={
//    spark.sql("drop table " + tableName)
    DF.write.mode("append").saveAsTable(tableName)
  }

  //表的表名
  val USER_VISIT_ACTION_TABLE = "db_UserBehaviors.user_visit_action"
  val USER_INFO_TABLE = "db_UserBehaviors.user_info"
  val PRODUCT_INFO_TABLE = "db_UserBehaviors.product_info"

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("MockData")

    val spark = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate()
    import spark.implicits._

    //模拟数据
    val userVisitActionData: Array[UserVisitAction] = this.mockUserVisitActionData()
    val userInfoData: Array[UserInfo] = this.mockUserInfo()
    val productInfo: Array[ProductInfo] = this.mockProductInfo()

    //将模拟数据封装成RDD
    val userVisitActionRDD = spark.sparkContext.makeRDD(userVisitActionData)
    val userInfoRDD = spark.sparkContext.makeRDD(userInfoData)
    val productInfoRDD = spark.sparkContext.makeRDD(productInfo)

    //创建数据库
//    spark.sql("create database db_UserBehaviors")
    spark.sql("show databases").show()
//    spark.sql("select * from db_UserBehaviors.user_info limit 10").show()

    //将用户访问数据转换成DF保存到hive中
    val userVisitActionDF = userVisitActionRDD.toDF()
    insertHive(spark,USER_VISIT_ACTION_TABLE,userVisitActionDF)

    //将用户信息表插入到hive中
    val userInfoDF = userInfoRDD.toDF()
    insertHive(spark,USER_INFO_TABLE,userInfoDF)

    //将商品信息表插入到hive中
    val productInfoDF = productInfoRDD.toDF()
    insertHive(spark,PRODUCT_INFO_TABLE,productInfoDF)

    spark.close()
  }
}
