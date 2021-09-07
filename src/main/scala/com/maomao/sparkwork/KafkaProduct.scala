package com.maomao.sparkwork

import com.alibaba.fastjson.JSONObject

import java.util
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.{Logger, LoggerFactory}


object KafkaProduct {
  private val logger: Logger = LoggerFactory.getLogger(KafkaProductStream.getClass)

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("spark-streaming-kafka-productMM").setMaster("local")
    val sc = new SparkContext(sparkConf)
    val props = new util.HashMap[String, Object]()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    var num = 0
    for(i<- 1 to 1000){
      Thread.sleep(500)
      val json = new JSONObject()
      json.put("name","jsson"+i)
      json.put("addr","25"+i)
      producer.send(new ProducerRecord("test",json.toString()))
      println(i)
    }
    sc.stop()
  }
}
