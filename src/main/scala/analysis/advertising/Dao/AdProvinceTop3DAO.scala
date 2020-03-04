package analysis.advertising.Dao

import analysis.advertising.bean.AdProvinceTop3
import commons.pool.{CreateMysqlPool, MySqlProxy}

import scala.collection.mutable.ArrayBuffer

/**
 * 各省份top3热门广告DAO实现类
 */
object AdProvinceTop3DAO {
  def updateBatch(adProvinceTop3s:Array[AdProvinceTop3])={
    //获取对象池单例对象
    val mySqlPool = CreateMysqlPool()
    //从对象池中提取对象
    val client: MySqlProxy = mySqlPool.borrowObject()

    //dateProvince可以实现一次去重，AdProvinceTop3中的数据都是由date province adid 组成
    //当只取date province时，一定会出现重复数据的情况
    val dateProvinces: ArrayBuffer[String] = ArrayBuffer[String]()

    for (adProvinceTop3 <- adProvinceTop3s){
      //组合新的key
      val key = adProvinceTop3.date + "_" + adProvinceTop3.province

      //dateProvince中不包含当前key才添加，以此来去重操作
      if (!dateProvinces.contains(key)){
        dateProvinces += key
      }
    }

    //根据去重后的date和province，进行批量删除操作，先将原来的数据全部删除
    val deleteSQL = "delete from ad_province_top3 where date=? and province=?"

    val deleteParamsList: ArrayBuffer[Array[Any]] = ArrayBuffer[Array[Any]]()

    for (dateProvince <- dateProvinces){

      val dateProvinceSplited = dateProvince.split("_")
      val date = dateProvinceSplited(0)
      val province = dateProvinceSplited(1)

      val params = Array[Any](date,province)
      deleteParamsList += params
    }

    client.executeBatch(deleteSQL,deleteParamsList.toArray)

    //批量插入传入进来的所有数据
    val insertSQL = "insert into ad_province_top3 values(?,?,?,?)"

    val insertParamsList: ArrayBuffer[Array[Any]] = ArrayBuffer[Array[Any]]()

    //将传入的数据转化为参数列表
    for (adProvinceTop3 <- adProvinceTop3s) {
      insertParamsList += Array[Any](adProvinceTop3.date,adProvinceTop3.province,adProvinceTop3.adid,adProvinceTop3.clickCount)
    }

    client.executeBatch(insertSQL,insertParamsList.toArray)

    //使用完成后将对象放回给对象池
    mySqlPool.returnObject(client)
  }
}
