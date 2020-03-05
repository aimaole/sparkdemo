package com.maomao.examples.streaming

import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object NetWordCount {
  def main(args: Array[String]): Unit = {
    /**
      * 初始化程序入口
      */
    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("NetWordCount")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc,Seconds(2))

    /*StreamingContext源码会createNewSparkContext,所以可以省略创建SparkContext对象。
    val ssc = new StreamingContext(conf,Seconds(2))*/

    /**
      * 通过程序入口获取DStream
      * 我们通过监听的方式获取数据，监听主机名和端口。只要一有数据就可以获取到，相当于通过Socket的方式
      */
    //ReceiverInputDStream就是个DStream，继承InputDStream继承DStream(就是一个抽象类,其实就是个HashMap(Time，RDD[T])一个时间点对应一个RDD )
    val dstream: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop04", 9999)

    /**
      * 对DStream流进行操作
      */
    //下面都是Transformation操作
    val wordCountDStream: DStream[(String, Int)] = dstream.flatMap(line => line.split(","))
      .map((_, 1))
      .reduceByKey(_ + _)
    //output的操作类似于RDD的Action
    wordCountDStream.print()  //把数据打印出来
    /**
      * 启动应用程序（固定操作）
      */
    //启动我们的程序
    ssc.start();
    //等待结束
    ssc.awaitTermination();
    //如果结束就释放资源
    ssc.stop();
  }
}
