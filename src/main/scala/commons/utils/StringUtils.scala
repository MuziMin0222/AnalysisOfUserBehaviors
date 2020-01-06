package commons.utils

import scala.collection.mutable

/**
 * 字符串工具类
 *
 */
object StringUtils {

  /**
   * 判断字符串是否为空
   * @param str 字符串
   * @return 是否为空
   */
  def isEmpty(str:String):Boolean = {
    str == null || "".equals(str)
  }

  /**
   * 判断字符串是否不为空
   * @param str 字符串
   * @return 是否不为空
   */
  def isNotEmpty(str:String):Boolean = {
    str != null && !"".equals(str)
  }

  /**
   * 截断字符串两侧的逗号
   * @param str 字符串
   * @return 字符串
   */
  def trimComma(str:String):String = {
    var result = ""
    if(str.startsWith(",")) {
      result = str.substring(1)
    }
    if(str.endsWith(",")) {
      result = str.substring(0, str.length() - 1)
    }
    result
  }

  /**
   * 补全两位数字
   * @param str
   * @return
   */
  def fulfuill(str: String):String = {
    if(str.length() == 2) {
      str
    } else {
      "0" + str
    }
  }

  /**
   * 从拼接的字符串中提取字段
   * @param str 字符串
   * @param delimiter 分隔符
   * @param field 字段
   * @return 字段值
   */
  def getFieldFromConcatString(str:String, delimiter:String, field:String):String = {
    try {
      val fields = str.split(delimiter);
      for(concatField <- fields) {
        if(concatField.split("=").length == 2) {
          val fieldName = concatField.split("=")(0)
          val fieldValue = concatField.split("=")(1)
          if(fieldName.equals(field)) {
            return fieldValue
          }
        }
      }
    } catch{
      case e:Exception => e.printStackTrace()
    }
    null
  }

  /**
   * 从拼接的字符串中给字段设置值
   * @param str 字符串
   * @param delimiter 分隔符
   * @param field 字段名
   * @param newFieldValue 新的field值
   * @return 字段值
   */
  def setFieldInConcatString(str:String, delimiter:String, field:String, newFieldValue:String):String = {

    val fieldsMap = new mutable.HashMap[String,String]()

    for(fileds <- str.split(delimiter)){
      var arra = fileds.split("=")
      if(arra(0).compareTo(field) == 0)
        fieldsMap += (field -> newFieldValue)
      else
        fieldsMap += (arra(0) -> arra(1))
    }
    fieldsMap.map(item=> item._1 + "=" + item._2).mkString(delimiter)
  }

}