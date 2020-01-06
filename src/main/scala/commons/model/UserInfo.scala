package commons.model

/**
 * 用户信息表
 *
 * @param user_id      用户的ID
 * @param username     用户的名称
 * @param name         用户的名字
 * @param age          用户的年龄
 * @param professional 用户的职业
 * @param city         用户所在的城市
 * @param sex          用户的性别
 */
case class UserInfo(user_id: Long,
                    username: String,
                    name: String,
                    age: Int,
                    professional: String,
                    city: String,
                    sex: String
                   )
