package com.maomao.sparkwork

import org.apache.spark.sql.SparkSession

object sparksqltext {
  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder.master("local").appName("sparksqlexample")
      .getOrCreate()
    val tt = sparkSession.read.json("E://testdata/data.txt")
    tt.createOrReplaceTempView {
      "user"
    }
    tt.show()
    sparkSession.sql("select * from user ").show()
    sparkSession.stop()


  }

}
