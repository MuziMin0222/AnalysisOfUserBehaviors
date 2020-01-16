package analysis.session.Function


import analysis.session.bean.Top10Category

import scala.collection.mutable.ArrayBuffer
import commons.constant.Constants
import commons.utils.StringUtils
import analysis.session._
import commons.conf.ConfigurationManager
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SaveMode
/**
 * 需求三:top10热门品类统计
 *
 * 在符合条件的用户行为数据中，获取点击、下单和支付数量排名前10的品类。
 * 在Top10的排序中，按照点击数量、下单数量、支付数量的次序进行排序，即优先考虑点击数量
 */

object UserSessionAnalysisFunction3 {
  val tuple = UserSessionAnalysisFunction1.Demand1()
  val session_id2detailRDD = tuple._3

  def demand3() = {

    //1、获取每一个session_id点击过，下单过，支付过的数量
    //获取所有产生过点击，下单，支付中任意行为的商品类别
    val categoryIdRDD = session_id2detailRDD.flatMap {
      case (session_id, userVisitAction) => {
        val list = ArrayBuffer[(Long, Long)]()

        //一个session中点击的商品类别ID
        if (userVisitAction.click_category_id != -1L) {
          list += ((userVisitAction.click_category_id, userVisitAction.click_category_id))
        }
        //一个session中下单的商品id集合
        if (userVisitAction.order_category_ids != null) {
          for (orderCategory <- userVisitAction.order_category_ids.split(",")) {
            list += ((orderCategory.toLong, orderCategory.toLong))
          }
        }
        //一个session中支付的商品类别ID集合
        if (userVisitAction.pay_category_ids != null) {
          for (payCategoryId <- userVisitAction.pay_category_ids.split(",")) {
            list += ((payCategoryId.toLong, payCategoryId.toLong))
          }
        }

        list
      }
    }

    //对重复的categoryId进行去重操作
    val distinctCategoryIdRDD = categoryIdRDD.distinct()

    //2、计算各品类的点击，下单和支付的次数
    //计算各品类的点击次数
    val clickCategoryId2CountRDD = getClickCategoryId2CountRDD()

    //计算各品类的下单次数
    val orderCategoryId2CountRDD = getOrderCategoryId2CountRDD()

    //计算各个品类的支付次数
    val payCategoryId2CounrRDD = getPayCategoryId2CountRDD()

    //3、join各品类和他的点击，下单，和支付的次数
    //将所有的品类信息和点击次数信息结合
    val clickJoinRDD = distinctCategoryIdRDD.leftOuterJoin(clickCategoryId2CountRDD)
      .map {
        case (categoryId, (cid, optionValue)) => {
          val clickCount = if (optionValue.isDefined) {
            optionValue.get
          } else {
            0L
          }

          val value = Constants.FIELD_CATEGORY_ID + "=" + categoryId + "|" + Constants.FIELD_CLICK_COUNT + "=" + clickCount
          (categoryId, value)
        }
      }
    //将连接后的点击信息和下单次数信息结合
    val OrderJoinRDD = clickJoinRDD.leftOuterJoin(orderCategoryId2CountRDD)
      .map {
        case (categoryId, (ovalue, optionValue)) => {
          val orderCount = if (optionValue.isDefined) {
            optionValue.get
          } else {
            0L
          }
          val value = ovalue + "|" + Constants.FIELD_ORDER_COUNT + "=" + orderCount
          (categoryId, value)
        }
      }
    //将连接后的订单信息和付款信息相结合
    val categoryId2CountRDD = OrderJoinRDD.leftOuterJoin(payCategoryId2CounrRDD)
      .map {
        case (categoryId, (ovalue, optionValue)) => {
          val payCount = if (optionValue.isDefined) {
            optionValue.get
          } else {
            0L
          }
          val value = ovalue + "|" + Constants.FIELD_PAY_COUNT + "=" + payCount
          (categoryId, value)
        }
      }

    //4、自定义二次排序CategorySortKey
    //5、将数据映射成<CategorySoryKey,Info>格式的RDD，然后进行二次排序(降序)
    val sortKey2CountRDD = categoryId2CountRDD.map {
      case (categoryId, line) => {
        val clickCount = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_CLICK_COUNT).toLong
        val orderCount = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_ORDER_COUNT).toLong
        val payCount = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_PAY_COUNT).toLong
        (CategorySoryKey(clickCount, orderCount, payCount), line)
      }
    }
    //降序排序
    val sortedCategoryCountRDD = sortKey2CountRDD.sortByKey(false)

    //6、用take(10)取出top10热门商品，并写入到mysql中
    val top10CategoryList = sortedCategoryCountRDD.take(10)
    val top10Category = top10CategoryList.map {
      case (categorySortkey, line) => {
        val categoryid = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_CATEGORY_ID).toLong
        val clikcCount = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_CLICK_COUNT).toLong
        val orderCount = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_ORDER_COUNT).toLong
        val payCount = StringUtils.getFieldFromConcatString(line, "\\|", Constants.FIELD_PAY_COUNT).toLong

        Top10Category(taskUUID, categoryid, clikcCount, orderCount, payCount)
      }
    }

    val top10CategoryRDD = sc.makeRDD(top10Category)

    //写入mysql数据库
    import spark.implicits._
    top10CategoryRDD.toDF().write
      .format("jdbc")
      .option("url",ConfigurationManager.config.getString(Constants.JDBC_URL))
      .option("dbtable",ConfigurationManager.config.getString(Constants.JDBC_TABLE_TOP10CATEGORY))
      .option("user",ConfigurationManager.config.getString(Constants.JDBC_USER))
      .option("password",ConfigurationManager.config.getString(Constants.JDBC_PASSWORD))
      .mode(SaveMode.Overwrite)
      .save()

    top10CategoryList

  }

  /**
   * 获取各个品类的支付次数RDD
   *
   * @return
   */
  def getPayCategoryId2CountRDD() = {
    //过滤数据并聚合
    session_id2detailRDD.filter {
      case (session_id, userVisitAction) => {
        userVisitAction.pay_category_ids != null
      }
    }.flatMap {
      case (session_id, userVisitAction) => {
        userVisitAction.pay_category_ids.split(",").map(item => (item.toLong, 1L))
      }
    }.reduceByKey(_ + _)
  }

  /**
   * 获取个品类的下单次数的RDD
   *
   * @return
   */
  def getOrderCategoryId2CountRDD() = {
    //过滤订单数据,并聚合
    session_id2detailRDD.filter {
      case (session_id, userVisitAction) => {
        userVisitAction.order_category_ids != null
      }
    }.flatMap {
      case (session, userVisitAction) => {
        userVisitAction.order_category_ids.split(",").map(item => (item.toLong, 1L))
      }
    }.reduceByKey(_ + _)
  }

  /**
   * 获取个品类点击次数的RDD
   *
   * @return 聚合后的RDD
   */
  def getClickCategoryId2CountRDD() = {
    //只将点击行为过滤出来
    val clickActionRDD = session_id2detailRDD.filter {
      case (session__id, userVisitAction) => {
        userVisitAction.click_category_id != -1L
      }
    }

    //获取每种类别的点击次数
    val clickCategoryIdRDD = clickActionRDD.map {
      case (session_id, userVisitAction) => {
        (userVisitAction.click_category_id, 1L)
      }
    }

    //计算各个品类的点击次数
    clickCategoryIdRDD.reduceByKey(_ + _)
  }
}
