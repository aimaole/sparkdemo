package com.maomao.sparkwork

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author maohongqi
 * @Date 2020/3/6 11:22
 * @Version 1.0
 **/
object WorldCountMM {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
//      .setMaster("local")
      .setAppName("wordCount")
    val sc = new SparkContext(conf)
    sc.textFile(args(0))
      .flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)
      .saveAsTextFile(args(1))
    sc.stop()
  }
}
