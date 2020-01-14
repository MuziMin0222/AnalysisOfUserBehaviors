package analysis.Demo

import commons.conf.ConfigurationManager
import commons.constant.Constants
import commons.utils.{ParamUtils, StringUtils, ValidUtils}
import net.sf.json.JSONObject

object Demo {
  def main(args: Array[String]): Unit = {
    val date = "fc5216ce783a4cebb983405e5c7b9915,sessionid=fc5216ce783a4cebb983405e5c7b9915|searchKeywords=国贸大厦,太古商场,温泉,蛋糕,重庆小面,火锅|clickCategoryIds=76,73,80,36,31|visitLength=3108|stepLength=25|startTime=2020-01-07 19:06:08|age=6|professional=professional7|city=city93|sex=female"

    //获取统计任务参数，即过滤参数
    val jsonStr = ConfigurationManager.config.getString(Constants.TASK_PARAMS)
    val taskParam = JSONObject.fromObject(jsonStr)
    println("taskParam:" + taskParam)

    val startAge = ParamUtils.getParam(taskParam, Constants.PARAM_START_AGE)
    println("startAge:" + startAge)

    val endAge = ParamUtils.getParam(taskParam, Constants.PARAM_END_AGE)
    println("endAge:" + endAge)

    val professionals = ParamUtils.getParam(taskParam, Constants.PARAM_PROFESSIONALS)
    println("professionals:" + professionals)

    val cites = ParamUtils.getParam(taskParam, Constants.PARAM_CITIES)
    println("cites:" + cites)

    val sex = ParamUtils.getParam(taskParam, Constants.PARAM_SEX)
    println("sex:" + sex)

    val keywords = ParamUtils.getParam(taskParam, Constants.PARAM_KEYWORDS)
    println("keywords:" + keywords)

    val categoryIds = ParamUtils.getParam(taskParam, Constants.PARAM_CATEGORY_IDS)
    println("categoryIds:" + categoryIds)

    //如果获取的值为null值，就进行空串拼接
    var _parameter =
      (if (startAge != null) {
        Constants.PARAM_START_AGE + "=" + startAge + "|"
      } else {
        ""
      }) +
        (if (endAge != null) {
          Constants.PARAM_END_AGE + "=" + endAge + "|"
        } else {
          ""
        }) +
        (if (professionals != null) {
          Constants.PARAM_PROFESSIONALS + "=" + professionals + "|"
        } else {
          ""
        }) +
        (if (cites != null) {
          Constants.PARAM_CITIES + "=" + cites + "|"
        } else {
          ""
        }) +
        (if (sex != null) {
          Constants.PARAM_SEX + "=" + sex + "|"
        } else {
          ""
        }) +
        (if (keywords != null) {
          Constants.PARAM_KEYWORDS + "=" + keywords + "|"
        } else {
          ""
        }) +
        (if (categoryIds != null) {
          Constants.PARAM_CATEGORY_IDS + "=" + categoryIds + "|"
        } else {
          ""
        })

    //将结尾的|线给删除
    if (_parameter.endsWith("\\|")) {
      _parameter = _parameter.substring(0, _parameter.length - 1)
    }
    val parameter = _parameter

    println("parameter:" + parameter)

    val startAge1 = StringUtils.getFieldFromConcatString(parameter,"\\|","startAge").toInt
    println("startAge1:" + startAge1)

    val endParamFieldStr = StringUtils.getFieldFromConcatString(parameter, "\\|", "endAge").toInt
    println("endParamFieldStr:" + endParamFieldStr)

    val dataFieldStr = StringUtils.getFieldFromConcatString(date,"\\|",Constants.FIELD_AGE)
    println("dataFieldStr:" + dataFieldStr)


    val SEX = !ValidUtils.in(date, Constants.FIELD_SEX, parameter, Constants.PARAM_SEX)
    println("SEX:" + SEX)

  }
}
