package analysis.product.Function

import analysis._
import analysis.product.bean.AreaTop3Product
import commons.conf.ConfigurationManager
import commons.constant.Constants
import commons.utils.ParamUtils
import net.sf.json.JSONObject
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SaveMode}

/**
 * 各区域Top3商品统计
 * 统计各个区域中Top3的热门商品，热门商品的评判指标是商品被点击的次数，对于user_visit_action表，click_product_id表示被点击的商品。
 */
object AreaTop3ProductFunc {

  def demand6() = {

    //注册自定义函数
    //自定义字符串连接函数
    spark.udf.register("concat_long_string", (v1: Long, v2: String, split: String) => v1.toString + split + v2)

    //从json中获取字段函数
    spark.udf.register("get_json_object", (json: String, field: String) => {
      val jsonObject = JSONObject.fromObject(json)
      jsonObject.getString(field)
    })

    //分组去重函数
    spark.udf.register("group_concat_distinct", new GroupConcatDistinctUDAF)

    //获取任务日期参数
    val startDate: String = ParamUtils.getParam(taskParam, Constants.PARAM_START_DATE)
    val endDate: String = ParamUtils.getParam(taskParam, Constants.PARAM_END_DATE)

    //查询用户指定日期范围内的点击行为数据（City_id,在哪个城市发送的点击行为）
    val cityid2clickActionRDD: RDD[(Long, Row)] = getCityId2ClickActionRDDByDate(startDate, endDate)
//    println("-------cityid2clickActionRDD-----------")
//    cityid2clickActionRDD.take(10).foreach(println)

    //查询城市信息 (city_id,城市信息)
    val cityid2cityInfoRDD: RDD[(Long, Row)] = getCityid2CityInfoRDD()
//    println("---------cityid2cityInfoRDD------------------")
//    cityid2cityInfoRDD.take(10).foreach(println)

    //生产商品基础信息临时表——tmp_click_product_basic
    generateTempClickProductBasicTable(cityid2clickActionRDD, cityid2cityInfoRDD)
//    println("----------tmp_click_product_basic------------------------")
//    spark.sql("select * from tmp_click_product_basic limit 10").show()

    //生成各区域个商品点击次数的临时表
    generateTempAreaProductClickCountTable()
//    println("----------tmp_area_product_click_count------------------")
//    spark.sql("select * from tmp_area_product_click_count limit 10").show()


    //生成包含完整商品信息的各区域个商品点击次数的临时表
    generateTempAreaFullProductClickCountTable()
//    println("--------tmp_area_fullprod_click_count----------")
//    spark.sql("select * from tmp_area_fullprod_click_count limit 10").show()

    //使用窗口函数获取各个区域内点击次数排名前3的热门商品
    val areaTop3ProductRDD: DataFrame = getAreaTop3ProductRDD()
//    println("-------------areaTop3ProductRDD--------------")
//    areaTop3ProductRDD.rdd.take(10).foreach(println)

    //将数据转换成df，并保存在mysql数据库中
    import spark.implicits._
    val areaTop3ProductDF: DataFrame = areaTop3ProductRDD.rdd.map(row => {
      AreaTop3Product(taskUUID,
        row.getAs[String]("area"),
        row.getAs[String]("area_level"),
        row.getAs[Long]("product_id"),
        row.getAs[String]("city_infos"),
        row.getAs[Long]("click_count"),
        row.getAs[String]("product_name"),
        row.getAs("product_status"))
    }).toDF

    areaTop3ProductDF.write
      .format("jdbc")
      .option("url", ConfigurationManager.config.getString(Constants.JDBC_URL))
      .option("dbtable", "area_top3_product")
      .option("user", ConfigurationManager.config.getString(Constants.JDBC_USER))
      .option("password", ConfigurationManager.config.getString(Constants.JDBC_PASSWORD))
      .mode(SaveMode.Append)
      .save()
  }

  /**
   * 获取各区域top3热门商品
   * 使用窗口函数先进行一个子查询，按照area进行分组，给每个分组内的数据，按照点击次数降序排序，打上一个组内的行号
   * 接着在外层查询中，过滤出各个组内的行号排名前3的数据
   *
   * @return DataFrame结果集
   */
  def getAreaTop3ProductRDD() = {
    /*
    华北、华东、华南、华中、西北、西南、东北
    A级：华北、华东
    B级：华南、华中
    C级：西北、西南
    D级：东北
     */
    val sql = "select area, " +
      "CASE " +
      "WHEN area='华北' OR area='华东' THEN 'A_Level' " +
      "WHEN area='华中' OR area='华南' THEN 'B_Level' " +
      "WHEN area='西南' OR area='西北' THEN 'C_Level' " +
      "ELSE 'D_Level' " +
      "END area_level, " +
      "city_infos, product_id, product_name, product_status, click_count from (" +
      "select area, city_infos, product_id, product_name, product_status, click_count," +
      "row_number() over(PARTITION BY area ORDER BY click_count DESC) rank from " +
      "tmp_area_fullprod_click_count) t where rank<=3"

    spark.sql(sql)
  }

  /**
   * 生成区域商品点击次数临时表（包含了商品的完整信息）
   */
  def generateTempAreaFullProductClickCountTable() = {
    /*
    将之前得到的各区域各商品点击次数表——product_id
    去关联商品信息表，product_id,product_name和product_status
    product_status要进行特殊处理，0,1分别代表了自营和第三方的商品，放在了一个json串里面
    get_json_object()函数：可以从json串中获取指定的字段的值
     */
    val sql = "select " +
      "tapcc.area," +
      "tapcc.product_id," +
      "tapcc.click_count," +
      "tapcc.city_infos," +
      "pi.product_name," +
      "if(get_json_object(pi.extend_info,'product_status')='0','Self','Third Party') product_status " +
      "from tmp_area_product_click_count tapcc " +
      "join db_userbehaviors.product_info pi on tapcc.product_id = pi.product_id"

    val df = spark.sql(sql)

    df.createOrReplaceTempView("tmp_area_fullprod_click_count")
  }

  /**
   * 生成各区域各商品点击次数临时表
   */
  def generateTempAreaProductClickCountTable() = {
    //按照area和product_id两个字段进行分组，计算出各区域个商品的点击次数，可以获取每个area下的每个product_id的城市信息进行拼接
    val sql = "select " +
      "area," +
      "product_id," +
      "count(*) click_count, " +
      "group_concat_distinct(concat_long_string(city_id,city_name,':')) city_infos " +
      "from tmp_click_product_basic " +
      "group by area,product_id"

    val df: DataFrame = spark.sql(sql)

    //各区域各商品的点击行为（以及额外的城市列表），再次将查询出来的数据注册为一个临时表
    df.createOrReplaceTempView("tmp_area_product_click_count")
  }

  /**
   * 生成点击商品基础信息临时表
   *
   * @param cityid2clickActionRDD 查询用户指定日期范围内的点击行为数据（City_id,在哪个城市发送的点击行为）
   * @param cityid2cityInfoRDD    查询城市信息 (city_id,城市信息)
   */
  def generateTempClickProductBasicTable(cityid2clickActionRDD: RDD[(Long, Row)], cityid2cityInfoRDD: RDD[(Long, Row)]) = {
    //执行join操作，进行点击行为数据和城市数据的关联
    val joinedRDD: RDD[(Long, (Row, Row))] = cityid2clickActionRDD.join(cityid2cityInfoRDD)

    //上上面获得的键值对RDD转换RDD<Row>
    val mappedRDD: RDD[(Long, String, String, Long)] = joinedRDD.map {
      case (cityid, (action, cityInfo)) => {
        val productId: Long = action.getLong(1)
        val cityName: String = cityInfo.getString(1)
        val area: String = cityInfo.getString(2)

        (cityid, cityName, area, productId)
      }
    }
    // group by area,product_id
    // 两个函数
    // UDF：concat2()，将两个字段拼接起来，用指定的分隔符
    // UDAF：group_concat_distinct()，将一个分组中的多个字段值，用逗号拼接起来，同时进行去重
    import spark.implicits._
    val df: DataFrame = mappedRDD.toDF("city_id", "city_name", "area", "product_id")

    //为df创建一个临时表
    df.createOrReplaceTempView("tmp_click_product_basic")
  }

  /**
   * 将城市信息封装成rdd
   *
   * @return （city_id，row）
   */
  def getCityid2CityInfoRDD() = {
    val cityInfo = Array((0L, "北京", "华北"), (1L, "上海", "华东"), (2L, "南京", "华东"), (3L, "广州", "华南"), (4L, "三亚", "华南"), (5L, "武汉", "华中"), (6L, "长沙", "华中"), (7L, "西安", "西北"), (8L, "成都", "西南"), (9L, "哈尔滨", "东北"))

    import spark.implicits._
    val cityInfoDF: DataFrame = sc.makeRDD(cityInfo).toDF("city_id", "city_name", "area")

    cityInfoDF.rdd.map(item => {
      (item.getAs[Long]("city_id"), item)
    })
  }

  /**
   * 查询指定日期范围内的点击行为数据
   *
   * @param startDate 起始日期
   * @param endDate   结束日期
   * @return 点击行为数据
   */
  def getCityId2ClickActionRDDByDate(startDate: String, endDate: String) = {
    //从user_visit_action中，查询用户访问行为数据，
    // 第一个限定：Click_product_id,限定为不为空的访问行为，那么就代表着点击行为
    // 第二个限定：在用户指定的日期范围内的数据
    val sql = "select " +
      "city_id," +
      "click_product_id " +
      "from db_UserBehaviors.user_visit_action " +
      "where click_product_id is not null and click_product_id != -1L " +
      "and date>='" + startDate + "' " +
      "and date<='" + endDate + "'"

    val clickActionDF: DataFrame = spark.sql(sql)

    clickActionDF.rdd.map(item => (item.getAs[Long]("city_id"), item))
  }
}
