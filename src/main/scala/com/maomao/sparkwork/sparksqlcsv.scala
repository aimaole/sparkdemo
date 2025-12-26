package com.maomao.sparkwork

import org.apache.spark.sql.SparkSession

object sparksqlcsv {
  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder.master("local").appName("sparksqlexample")
      .getOrCreate()
    // format指定读取csv文件。
    // header是否指定头部行作为schema。
    // multiLine在单元格中可能因为字数多有换行，但是不指定这个参数，处理数据时可能会报错。指定这个参数为true，可以将换行的单元格合并为1行。
    // delimiter 分隔符，默认为逗号,
    // nullValue 指定一个字符串代表 null 值
    // quote 引号字符，默认为双引号"
    // inferSchema 自动推测字段类型
    //读取
    //    val options = Map("header" -> "true", "delimiter" -> "\t", "path" -> "hdfs://172.xx.xx.xx:9000/test")
    //    val datarDF= spark.read.options(options).format("com.databricks.spark.csv").load()

    val dataFrame = sparkSession.read
      .option("header", "true")
      .option("multiLine", true)
      .option("delimiter", ";")
      .option("inferSchema", true)
      .csv("D:\\study\\sparkdemo\\src\\main\\resources\\people.csv")
    dataFrame.createTempView("people")
    //    dataFrame.foreach(line=>{
    //      val name = line.getAs[String]("name")
    //      val age = line.getAs[Integer]("age")
    //      println(name)
    //      println(age)
    //
    //    })
    //保存
    //val saveoptions = Map("header" -> "true", "delimiter" -> "\t", "path" -> "hdfs://172.xx.xx.xx:9000/test")
    //datawDF.write.format("com.databricks.spark.csv").mode(SaveMode.Overwrite).options(saveoptions).save()
    //    dataFrame.write
    //      .option("header", true)
    //      .option("delimiter", ";")
    //      .csv("D:\\study\\sparkdemo\\src\\main\\resources\\peoplemm.csv")
    val dataFrame1 = sparkSession.sql("select name,age from people")
    dataFrame1.show()
    dataFrame1.createTempView("people_name")
    val frame = sparkSession.sql("select name from people_name")
    frame.show()
    frame.write
      .option("header", true)
      .option("delimiter", ";")
      .csv("D:\\study\\sparkdemo\\src\\main\\resources\\peoplemm")
    sparkSession.stop()


  }

}
