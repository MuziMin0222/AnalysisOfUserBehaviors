package commons.utils

/**
 * 校验工具类
 *
 */
object ValidUtils {

  /**
   * 校验数据中的指定字段，是否在指定范围内
   * @param data 数据
   * @param dataField 数据字段
   * @param parameter 参数（过滤条件组成的字符串）
   * @param startParamField 起始参数字段
   * @param endParamField 结束参数字段
   * @return 校验结果
   */
  def between(data:String, dataField:String, parameter:String, startParamField:String, endParamField:String):Boolean = {
    var flag = false

    val startParamFieldStr = StringUtils.getFieldFromConcatString(parameter, "\\|", startParamField)
    val endParamFieldStr = StringUtils.getFieldFromConcatString(parameter, "\\|", endParamField)
    if(startParamFieldStr == null || endParamFieldStr == null) {
      flag = true
      return flag
    }

    val startParamFieldValue = startParamFieldStr.toInt
    val endParamFieldValue = endParamFieldStr.toInt

    val dataFieldStr = StringUtils.getFieldFromConcatString(data, "\\|", dataField)
    if(dataFieldStr != null) {
      val dataFieldValue = dataFieldStr.toInt
      if(dataFieldValue >= startParamFieldValue && dataFieldValue <= endParamFieldValue) {
        flag = true
        return flag
      } else {
        return flag
      }
    }
    flag
  }

  /**
   * 校验数据中的指定字段，是否有值与参数字段的值相同
   * @param data 数据
   * @param dataField 数据字段
   * @param parameter 参数（从commerce.properties中获取的过滤条件组成的字符串）
   * @param paramField 参数字段
   * @return 校验结果
   */
  def in(data:String, dataField:String, parameter:String, paramField:String):Boolean = {
    var flag = true

    val paramFieldValue = StringUtils.getFieldFromConcatString(parameter, "\\|", paramField)
    if(paramFieldValue == null) {
      return flag
    }
    val paramFieldValueSplited = paramFieldValue.split(",")

    val dataFieldValue = StringUtils.getFieldFromConcatString(data, "\\|", dataField)
    if(dataFieldValue != null && dataFieldValue != "-1") {
      val dataFieldValueSplited = dataFieldValue.split(",")

      for(singleDataFieldValue <- dataFieldValueSplited) {
        for(singleParamFieldValue <- paramFieldValueSplited) {
          if(singleDataFieldValue.compareTo(singleParamFieldValue) ==0) {
            return flag
          }
        }
      }
    }
    flag
  }

  /**
   * 校验数据中的指定字段，是否在指定范围内
   * @param data 数据
   * @param dataField 数据字段
   * @param parameter 参数
   * @param paramField 参数字段
   * @return 校验结果
   */
  def equal(data:String, dataField:String, parameter:String, paramField:String):Boolean = {
    var flag = true

    val paramFieldValue = StringUtils.getFieldFromConcatString(parameter, "\\|", paramField)
    if(paramFieldValue == null) {
      flag = true
      return flag
    }else {
      flag = false
    }

    val dataFieldValue = StringUtils.getFieldFromConcatString(data, "\\|", dataField)
    if(dataFieldValue != null) {
      if(dataFieldValue.compareTo(paramFieldValue) == 0) {
        flag = true
        return flag
      }else{
        flag = false
      }
    }
    flag
  }

}