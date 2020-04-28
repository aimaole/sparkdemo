package com.maomao.sparkwork

import org.apache.kafka.clients.producer.{ProducerConfig, RecordMetadata}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.{Logger, LoggerFactory}


/**
 * spark streaming 整合 kafka
 */
object KafkaProductStream {
  private val logger: Logger = LoggerFactory.getLogger(KafkaProductStream.getClass)

  def main(args: Array[String]): Unit = {


    val ssc: StreamingContext = {
      val sparkConf = new SparkConf().setAppName("spark-streaming-kafka-productMM")
      new StreamingContext(sparkConf, Seconds(5))
    }

    ssc.checkpoint("/home/spark/data/checkpoint-directory")

    // 初始化KafkaSink,并广播
    val kafkaProducer: Broadcast[KafkaSink[String, String]] = {
      val kafkaProducerConfig: Map[String, Object] = Map[String, Object](
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "test.mm.com:9092",
        //        ProducerConfig.ACKS_CONFIG -> properties.getProperty("kafka1.acks"),
        //        ProducerConfig.RETRIES_CONFIG -> properties.getProperty("kafka1.retries"),
        //        ProducerConfig.BATCH_SIZE_CONFIG -> properties.getProperty("kafka1.batch.size"),
        //        ProducerConfig.LINGER_MS_CONFIG -> properties.getProperty("kafka1.linger.ms"),
        //        ProducerConfig.BUFFER_MEMORY_CONFIG -> properties.getProperty("kafka1.buffer.memory"),
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer],
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer]
      )
      //add comment, make it lazy
      if (logger.isInfoEnabled) {
        logger.info("kafka producer init done!")
      }
      ssc.sparkContext.broadcast(KafkaSink[String, String](kafkaProducerConfig))
    }

    def getKafkaProducerParams(): Map[String, Object] = {
      Map[String, Object](
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "test.mm.com:9092",
        //        ProducerConfig.ACKS_CONFIG -> properties.getProperty("kafka1.acks"),
        //        ProducerConfig.RETRIES_CONFIG -> properties.getProperty("kafka1.retries"),
        //        ProducerConfig.BATCH_SIZE_CONFIG -> properties.getProperty("kafka1.batch.size"),
        //        ProducerConfig.LINGER_MS_CONFIG -> properties.getProperty("kafka1.linger.ms"),
        //        ProducerConfig.BUFFER_MEMORY_CONFIG -> properties.getProperty("kafka1.buffer.memory"),
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer],
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer]
      )
    }


    //    import java.util.concurrent.Future
    //    import org.apache.kafka.clients.producer.RecordMetadata

    val stream: DStream[String] = ssc.textFileStream("/home/spark/data/in/aa.txt")
    val kayvStream: DStream[(String, String)] = stream.map(line => {
      val key = line.split(" ")(0)
      val value = line
      (key, value)
    })
    //    stream.foreachRDD(rdd => {
    //      rdd.foreachPartition(partitionOfRecords => {
    //        val metadata: Stream[Future[RecordMetadata]] = partitionOfRecords.map(record => {
    //          kafkaProducer.value.send("testMM", new Gson().toJson(record))
    //        })
    //          .toStream
    //        metadata.foreach(data => {
    //          data.get()
    //        })
    //      })
    //    })
    //输出到kafka
    kayvStream.foreachRDD(rdd => {
      if (!rdd.isEmpty) {
        rdd.foreach(record => {
          kafkaProducer.value.send("testMM", record._1.toString, record._2.toString)
          println(record._2.toString)
          // do something else
        })
      }
    })

    ssc.start()
    ssc.awaitTermination()


  }
}
