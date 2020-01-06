package commons.utils

import net.sf.json.JSONObject

/**
 * 参数工具类
 *
 */
object ParamUtils {

  /**
   * 从JSON对象中提取参数
   * @param jsonObject JSON对象
   * @return 参数
   */
  def getParam(jsonObject:JSONObject, field:String):String = {
    jsonObject.getString(field)
  }

}