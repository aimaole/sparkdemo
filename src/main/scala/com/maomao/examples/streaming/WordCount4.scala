package com.maomao.examples.streaming
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object WordCount4 {

  def main(args: Array[String]): Unit = {
    //1.创建StreamingContext
    //spark.master should be set as local[n], n > 1
    val conf = new SparkConf().setAppName("wc").setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    val ssc = new StreamingContext(sc, Seconds(5)) //5表示5秒中对数据进行切分形成一个RDD
    //2.监听Socket接收数据
    //ReceiverInputDStream就是接收到的所有的数据组成的RDD,封装成了DStream,接下来对DStream进行操作就是对RDD进行操作
    val dataDStream: ReceiverInputDStream[String] = ssc.socketTextStream("192.168.72.131", 9999)
    //3.操作数据
    val wordDStream: DStream[String] = dataDStream.flatMap(_.split(" "))
    val wordAndOneDStream: DStream[(String, Int)] = wordDStream.map((_, 1))
    //4.使用窗口函数进行WordCount计数
    val wordAndCount: DStream[(String, Int)] = wordAndOneDStream.reduceByKeyAndWindow((a: Int, b: Int) => a + b, Seconds(10), Seconds(5))
    val sorteDStream: DStream[(String, Int)] = wordAndCount.transform(rdd => {
      val sortedRDD: RDD[(String, Int)] = rdd.sortBy(_._2, false) //逆序/降序
      println("===============top3==============")
      sortedRDD.take(3).foreach(println)
      println("===============top3==============")
      sortedRDD
    }
    )
    //No output operations registered, so nothing to execute
    sorteDStream.print
    ssc.start() //开启
    ssc.awaitTermination() //等待优雅停止
  }

}