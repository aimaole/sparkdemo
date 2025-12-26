package com.maomao.sparkwork

import org.apache.spark.sql.SparkSession

object WorldCountMM {
  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder()
      .appName("wordCount")
      .master("local")
      .getOrCreate()
    val linesDF = sparkSession.read.text("src/main/resources/wc")

//     将每一行拆分成单词，并计算单词频次
    import sparkSession.implicits._
    val wordCountsDF = linesDF
      .flatMap(_.getString(0).split(" "))
      .filter(!_.isBlank)
      .groupBy("value")
      .count()
      .select($"value".alias("field"),$"count".alias("sum")).createTempView("table")
    val frame = sparkSession.sql("select * from table")

    frame.show()
//      .select($"count".alias("sum"))

    // 打印计数结果
//    wordCountsDF.show()

    // 停止SparkSession对象
    sparkSession.stop()


//    val conf = new SparkConf()
////      .setMaster("local")
//      .setAppName("wordCount")
//    val sc = new SparkContext(conf)
//    sc.textFile(args(0))
//      .flatMap(_.split(" "))
//      .map((_, 1))
//      .reduceByKey(_ + _)
//      .saveAsTextFile(args(1))
//    sc.stop()
  }
}
