package com.maomao.sparkwork

import org.apache.spark.sql.SparkSession

object sparksqltext {
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
    val dataFrame = sparkSession.read
      .option("header", "true")
      .option("multiLine", true)
      .option("delimiter",";")
      .option("inferSchema",true)
      .csv("D:\\study\\sparkdemo\\src\\main\\resources\\people.csv")
    dataFrame.createTempView("people")
    sparkSession.sql("select * from people")
    sparkSession.stop()


  }

}
