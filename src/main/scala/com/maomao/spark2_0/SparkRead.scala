package com.maomao.spark2_0

import org.apache.spark.sql.SparkSession

object SparkRead {
  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder().master("local").appName("sparkStudy").getOrCreate()
    sparkSession.conf.set("spark.sql.shuffle.partitions", "5")

    val flightData2015 = sparkSession
      .read
      .option("inferSchema", "true")
      .option("header", "true")
      .csv("D:\\study\\Spark-The-Definitive-Guide-master\\data\\flight-data\\csv\\2015-summary.csv")

    flightData2015.createOrReplaceTempView("flight_data_2015")

    //    val sqlWay = sparkSession.sql("SELECT DEST_COUNTRY_NAME, count(1) FROM flight_data_2015 GROUP BY DEST_COUNTRY_NAME ")
    //    sqlWay.explain
    //
    //    val dataFrameWay = flightData2015.groupBy("DEST_COUNTRY_NAME").count()
    //    dataFrameWay.explain

//    sparkSession.sql("SELECT max(count) from flight_data_2015").show()
//    import org.apache.spark.sql.functions.max
//    flightData2015.select(max("count")).show()

    val maxSql = sparkSession.sql("SELECT DEST_COUNTRY_NAME, sum(count) as destination_total FROM flight_data_2015 " +
      "GROUP BY DEST_COUNTRY_NAME " +
      "ORDER BY sum(count) " +
      "DESC LIMIT 5 ")
    maxSql.explain()
//      .show()
    import org.apache.spark.sql.functions.desc
    flightData2015
      .groupBy("DEST_COUNTRY_NAME")
      .sum("count")
      .withColumnRenamed("sum(count)", "destination_total")
      .sort(desc("destination_total"))
      .limit(5)
      .explain()
//      .show()

  }

}
