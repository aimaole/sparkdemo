package com.maomao.sparkwork

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object sparksql {
  case class Person(id: Int, name: String, sex: String, age: Int)
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("sparksql").setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._
    sqlContext.read.textFile()
    val lineRDD = sc.textFile("E://sparksql.txt").map(_.split(" "))
    val personRDD = lineRDD.map(x => Person(x(0).toInt, x(1), x(2), x(3).toInt))
    val personDF = personRDD.toDF()
    personDF.show
    personDF.registerTempTable("t_person")
    sqlContext.sql("select * from t_person order by age desc limit 2").show
    sqlContext.sql("desc t_person").show
    sc.stop()
  }

}
