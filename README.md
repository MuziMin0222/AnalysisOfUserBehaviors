## 0、环境的介绍：
1. spark 2.4.4
2. scala 2.11.8
3. hive 3.1.2
4. mysql 5.7.28
5. kafka_2.12-2.3.0
6. jdk 1.8.0_192
7. hadoop 2.9.2
8. zookeeper-3.5.5
9. Ubuntu 18.04
10. Windows10
## 1、程序中包的解释
#### Commons包：公共模块包
- conf：配置工具类，获取commerce.properties文件中的所有配置信息，
使用户可以通过对象的方式访问commerce.properties中的所有配置
- constant：常量接口，包括项目中所需要使用的所有常量
- model： Spark SQL样例类，包括Spark SQL中的用户访问动作表、
用户信息表、产品表的样例类
- pool：MySQL连接池，通过自定义MySQL连接池，实现对MySQL数据库
的操作
- utils：工具类，提供了日期时间工具类、数字格式工具类、参数工具类、字符串工具类、校验工具类等工具类，
里面的类有:
1. DateUtils：时间工具类，负责时间的格式化、判断时间先后、计算时间差值、获取指定日期等工作
2. NumberUtils：数字工具类，负责数字的格式化工作，将Double类型的数字精确为指定位数的小数
3. ParamUtils：参数工具类，负责从JSON对象中提取参数
4. StringUtils:字符串工具类,负责字符串是否为空判断、字符串截断与补全、从拼接字符串中提取字段、给拼接字符串中字段设置值等工作
5. ValidUtils:校验工具类,负责校验数据中的指定字段是否在指定范围范围内、校验数据中的指定字段中是否有值与参数字段相同、校验数据中的指定字段是否与参数字段相同等工作
#### mock包：模拟数据产生包
- MockDataGenerate：离线模拟数据生成，负责生成离线模拟数据
并写入Hive表中，模拟数据包括用户行为信息、用户信息、产品数据
信息等
- MockRealtimeDataGenerate：实时模拟数据生成，负责生成实时
模拟数据并写入Kafka中，实时模拟数据为实时广告数据
#### analysis包：数据分析包
- session：用户访问session统计  
  - session访问步长/访问时长占比统计
  - 按比例随机抽取session
  - top10热门品类统计
  - top10热门品类活跃session统计
- page：页面单跳转化率统计
- product：区域热门商品统计
- advertising：广告流量实时统计
  - 动态黑名单实时统计
  - 各省各城市广告流量实时统计
  - 各省热门广告实时统计
  - 最近一小时广告点击量实时统计

## 2、数据库设计
#### mysql中commerce数据库
- session_aggr_stat：保存session访问步长占比统计的结果
- session_random_extract：保存session随机抽取的结果
- top10_category：保存Top10热门品类统计的结果
- top10_session：保存Top10热门品类的Top10活跃Session统计的结果
- page_split_convert_rate：保存页面单跳转化率统计的结果
- area_top3_product：保存各区域Top3商品统计的结果
- ad_user_click_count：维护动态黑名单的表
- ad_blacklist：黑名单列表
- ad_stat：各省各城市广告流量实时统计结果
- ad_province_top3：各省热门广告实时统计结果
- ad_click_trend：最近一小时广告点击量实时统计结果
#### hive中db_UserBehaviors数据库
- user_visit_action表：存放的是用户行为（点击，搜索，下单，付款四种行为）
- user_info表：存放的是用户信息
- product_info表：存放的是产品信息

## 3、注意事项
- hive数据库元数据总是出
Unable to instantiate org.apache.hadoop.hive.ql.metadata.SessionHiveMetaStoreClient问题；
执行hive --service metastore &
- hive中删除有表的数据库：drop database 数据库名字 cascade;
