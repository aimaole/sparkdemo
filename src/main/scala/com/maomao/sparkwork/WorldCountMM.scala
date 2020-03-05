package com.maomao.sparkwork

import org.apache.spark.{SparkConf, SparkContext}

object WorldCountMM {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("wordCount")
    val sc = new SparkContext(conf)
    sc.textFile("E://test.txt")
      .flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)
//      .foreach(println)
      .saveAsTextFile("E://tessssssst")
    sc.stop()
  }
}
