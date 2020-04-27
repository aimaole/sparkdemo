package com.maomao.sparkwork

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

    val text = sc.textFile("D:\\study\\sparkdemo\\src\\main\\resources\\wc")
    text.foreachPartition(f=>{
      f.foreach(line=>{
        val props = new util.HashMap[String, Object]()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "skyeye.qianxin.com:9092")
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer")
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer")
        println(line)
        val producer = new KafkaProducer[String, String](props)
        val message = new ProducerRecord[String, String]("testMM", line.split("")(0), line)
        producer.send(message)
      })
    })
    sc.stop()
  }
}
