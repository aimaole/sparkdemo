package com.maomao.sparkwork

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * spark streaming 整合 kafka
  */
object KafkaDirectStream {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("KafkaDirectStream").setMaster("local[2]")
    val streamingContext = new StreamingContext(sparkConf, Seconds(5))

    val kafkaParams = Map[String, Object](
      /*
       * 指定 broker 的地址清单，清单里不需要包含所有的 broker 地址，生产者会从给定的 broker 里查找其他 broker 的信息。
       * 不过建议至少提供两个 broker 的信息作为容错。
       */
      "bootstrap.servers" -> "hadoop001:9092",
      /*键的序列化器*/
      "key.deserializer" -> classOf[StringDeserializer],
      /*值的序列化器*/
      "value.deserializer" -> classOf[StringDeserializer],
      /*消费者所在分组的 ID*/
      "group.id" -> "spark-streaming-group",
      /*
       * 该属性指定了消费者在读取一个没有偏移量的分区或者偏移量无效的情况下该作何处理:
       * latest: 在偏移量无效的情况下，消费者将从最新的记录开始读取数据（在消费者启动之后生成的记录）
       * earliest: 在偏移量无效的情况下，消费者将从起始位置读取分区的记录
       */
      "auto.offset.reset" -> "latest",
      /*是否自动提交*/
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )
    
    /*可以同时订阅多个主题*/
    val topics = Array("spark-streaming-topic")
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      /*位置策略*/
      PreferConsistent,
      /*订阅主题*/
      Subscribe[String, String](topics, kafkaParams)
    )

    /*打印输入流*/
    stream.map(record => (record.key, record.value)).print()

    streamingContext.start()
//    streamingContext.awaitTermination()
    streamingContext.stop()
  }
}
