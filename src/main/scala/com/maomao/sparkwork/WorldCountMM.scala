package com.maomao.sparkwork

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @Author maohongqi
 * @Date 2020/3/6 11:22
 * @Version 1.0
 **/
object WorldCountMM {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("wordCount")
    val sc = new SparkContext(conf)
    sc.textFile("D:\\study\\sparkdemo\\src\\main\\resources\\wc")
      .flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)
      .foreach(println)
//      .saveAsTextFile("E://tessssssst")
    sc.stop()
  }
}
