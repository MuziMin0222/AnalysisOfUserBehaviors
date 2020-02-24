package analysis.product.Function

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, StringType, StructField, StructType}

//用户自定义聚合函数
class GroupConcatDistinctUDAF extends UserDefinedAggregateFunction {
  //函数输入的数据结构
  override def inputSchema: StructType = StructType(StructField("cityInfo", StringType) :: Nil)

  //计算时的数据结构
  override def bufferSchema: StructType = StructType(StructField("bufferCityInfo", StringType) :: Nil)

  //函数返回的数据结构
  override def dataType: DataType = StringType

  //函数是否和要求的输出类型一致
  override def deterministic: Boolean = true

  //计算之前的缓冲区的初始值
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = ""
  }

  //根据查询结果更新缓冲区数据,一个一个将组内的字段值传递进来，实现逻辑上的拼接
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    //缓冲中的已经拼接过的城市信息串
    var bufferCityInfo: String = buffer.getString(0)

    //刚刚传递进来的某个城市的信息
    val cityInfo: String = input.getString(0)

    //去重逻辑，判断之前没有拼接过某个城市信息，那么这里才可以接下去拼接新的城市信息
    if (!bufferCityInfo.contains(cityInfo)) {
      if ("".equals(bufferCityInfo)) {
        bufferCityInfo += cityInfo
      } else {
        bufferCityInfo += "," + cityInfo
      }

      buffer.update(0, bufferCityInfo)
    }
  }

  //将多个节点的缓冲区合并
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    var bufferCityInfo1: String = buffer1.getString(0)
    val bufferCityInfo2: String = buffer2.getString(0)

    for (cityInfo <- bufferCityInfo2.split(",")) {
      if (!bufferCityInfo1.contains(cityInfo)) {
        if ("".equals(bufferCityInfo1)) {
          bufferCityInfo1 += cityInfo
        }else{
          bufferCityInfo1 += "," + cityInfo
        }
      }
    }

    buffer1.update(0,bufferCityInfo1)
  }

  //计算逻辑
  override def evaluate(buffer: Row): Any = {
    buffer.getString(0)
  }
}
