package com.maomao.sparkwork

import org.apache.spark.sql.SparkSession

import org.elasticsearch.spark

import org.elasticsearch.spark.rdd.EsSpark

object SparkES {

  case class Trip(content: String)

  def main(args: Array[String]): Unit = {

    println("Hello World!")

    System.setProperty("hadoop.home.dir", "G:\\hadoop_home")

    val spark = SparkSession.builder()

      .appName("SparkTest")

      .master("local[5]")

      .config("es.index.auto.create", "true")

      .config("pushdown", "true")

      .config("es.nodes", "192.168.2.5")

      .config("es.port", "9200")

      .config("es.nodes.wan.only", "true")

      .getOrCreate()

    //从ES中读取数据

    val sparkDF = spark.sqlContext.read.format("org.elasticsearch.spark.sql").load("index/external")

    sparkDF.take(10).foreach(println(_))

    import spark.implicits._

    val data = spark.read.textFile("g:\\mydata\\*")

    //写入到ES，一定要按照这个格式，因为这种格式才带有元数据信息，content就是ES中的列名

    val rdd = data.rdd.map {

      x => Trip(x)

    }

    EsSpark.saveToEs(rdd, "index/external")

    spark.stop()

  }

}
