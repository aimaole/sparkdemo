package com.maomao.sparkwork

import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object sparksql {
  case class Person(id: Int, name: String, sex: String, age: Int)
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("sparksql").setMaster("local")
    val session = SparkSession.builder().config(conf).getOrCreate()
    val dataFrame = session.read.json("D:\\study\\sparkdemo\\src\\main\\resources\\people.json")
    dataFrame.createTempView("table")
    session.sql("select * from table").show()
    session.stop()
  }

}
