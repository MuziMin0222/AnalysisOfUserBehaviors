package mock

import java.util.Properties

import commons.conf.ConfigurationManager
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
 * 模拟实时数据生成
 * 时间点: 当前时间毫秒
 * userId: 0 - 99
 * 省份、城市 ID相同 ： 1 - 9
 * adid: 0 - 19
 * ((0L,"北京","北京"),(1L,"上海","上海"),(2L,"南京","江苏省"),(3L,"广州","广东省"),(4L,"三亚","海南省"),(5L,"武汉","湖北省"),(6L,"长沙","湖南省"),(7L,"西安","陕西省"),(8L,"成都","四川省"),(9L,"哈尔滨","东北省"))
 * 格式 ：timestamp province city userid adid
 * 某个时间点 某个省份 某个城市 某个用户 某个广告
 */
object MockRealTimeData {
  def generateMockData() ={
    val array = ArrayBuffer[String]()
    val random = new Random()

    //模拟实时数据：timestamp  province  city  userID  ad_id
    for (i <- 0 to 50) {
      val timeStamp = System.currentTimeMillis()
      val province = random.nextInt(10)
      val city = province
      val ad_id = random.nextInt(20)
      val user_id = random.nextInt(100)

      //拼接数据
      array += timeStamp + " " + province + " " + city + " " + user_id + " " + ad_id
    }
    array.toArray
  }

  //创建kafka的生产者
  def createKafkaProducer(broker:String)={
    //创建配置对象
    val prop = new Properties()

    //添加配置
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,broker)
    prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer")
    prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer")

    //根据配置创建kafka生产者
    new KafkaProducer[String,String](prop)
  }

  def main(args: Array[String]): Unit = {
    //获取配置文件commerce.properties中kafka的配置参数
    val broker = ConfigurationManager.config.getString("kafka.broker.list")
    val topic = ConfigurationManager.config.getString("kafka.topics")

    //创建kafka生产者者
    val kafkaProducer = createKafkaProducer(broker)

    while (true){
      //随机产生实时数据并通过kafka生产者发送到kafka集群中
      for (i <- generateMockData()){
        kafkaProducer.send(new ProducerRecord[String,String](topic,i))
      }
      //系统休息五秒再进行发送
      Thread.sleep(5000)
    }
  }
}
