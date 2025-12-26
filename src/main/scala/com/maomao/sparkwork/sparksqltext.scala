package com.maomao.sparkwork

import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

object sparksqltext {
  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder.master("local").appName("sparksqltxt").getOrCreate()
    val rdd = sparkSession.sparkContext.textFile("D:\\study\\sparkdemo\\src\\main\\resources\\people.txt")
    val rowrdd = rdd.map(x => x.split(",")).map(attrs => Row(attrs(0), attrs(1).trim.toInt))
    //创建schema信息
    val schema = StructType(List(
      StructField("name", StringType, true),
      StructField("age", IntegerType, true)
    ))
    //创建dataFrame
    val df = sparkSession.createDataFrame(rowrdd, schema);
    df.createTempView("person")
    sparkSession.sql("select * from person").show()
    sparkSession.stop()
  }

}
