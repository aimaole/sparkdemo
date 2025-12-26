package com.maomao.examples.streaming

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.{SparkConf, SparkContext}

object WordCount2 {
  def main(args: Array[String]): Unit = {
    //1.创建StreamingContext
    //spark.master should be set as local[n], n > 1
    val conf = new SparkConf().setAppName("wc").setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    val ssc = new StreamingContext(sc,Seconds(5))//5表示5秒中对数据进行切分形成一个RDD
    //requirement failed: ....Please set it by StreamingContext.checkpoint().
    //注意:我们在下面使用到了updateStateByKey对当前数据和历史数据进行累加
    //那么历史数据存在哪?我们需要给他设置一个checkpoint目录
    ssc.checkpoint("e://checkpoint")//开发中HDFS
    //2.监听Socket接收数据
    //ReceiverInputDStream就是接收到的所有的数据组成的RDD,封装成了DStream,接下来对DStream进行操作就是对RDD进行操作
    val dataDStream: ReceiverInputDStream[String] = ssc.socketTextStream("192.168.72.131",9999)
    //3.操作数据
    val wordDStream: DStream[String] = dataDStream.flatMap(_.split(" "))
    val wordAndOneDStream: DStream[(String, Int)] = wordDStream.map((_,1))
    //val wordAndCount: DStream[(String, Int)] = wordAndOneDStream.reduceByKey(_+_)
    //====================使用updateStateByKey对当前数据和历史数据进行累加====================
    val wordAndCount: DStream[(String, Int)] =wordAndOneDStream.updateStateByKey(updateFunc)
    wordAndCount.print()
    ssc.start()//开启
    ssc.awaitTermination()//等待优雅停止
  }
  //currentValues:当前批次的value值,如:1,1,1 (以测试数据中的hadoop为例)
  //historyValue:之前累计的历史值,第一次没有值是0,第二次是3
  //目标是把当前数据+历史数据返回作为新的结果(下次的历史数据)
  def updateFunc(currentValues:Seq[Int], historyValue:Option[Int] ):Option[Int] ={
    val result: Int = currentValues.sum + historyValue.getOrElse(0)
    Some(result)
  }
}
