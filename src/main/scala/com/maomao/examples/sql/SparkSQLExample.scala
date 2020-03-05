package com.maomao.examples.sql

import org.apache.spark.sql.Row
// $example on:init_session$
import org.apache.spark.sql.SparkSession
// $example off:init_session$
// $example on:programmatic_schema$
// $example on:data_types$
import org.apache.spark.sql.types._

object SparkSQLExample {

  case class Person(name: String, age: Long)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Spark SQL basic example")
      .master("local[4]")
      .getOrCreate()

//    runBasicDataFrameExample(spark)
//    runDatasetCreationExample(spark)
    runInferSchemaExample(spark)
//    runProgrammaticSchemaExample(spark)
    spark.stop()
  }

  private def runDatasetCreationExample(spark: SparkSession): Unit = {
    import spark.implicits._
    val caseClassDS = Seq(Person("Andy", 32)).toDS()
    caseClassDS.show()
    val primitiveDS = Seq(1, 2, 3).toDS()
    primitiveDS.map(_ + 1).collect() // Returns: Array(2, 3, 4)

    val path = "src/main/resources/people.json"
    val peopleDS = spark.read.json(path).as[Person]
    peopleDS.show()
  }

  private def runBasicDataFrameExample(spark: SparkSession): Unit = {
    val df = spark.read.json("src/main/resources/people.json")
    df.show()

    df.printSchema()
    df.select("name").show()
    df.select("name", "age").show()
    import spark.implicits._
    df.select($"name", $"age" + 1).show()

    df.filter($"age" > 21).show()

    df.groupBy("age").count().show()
    df.createOrReplaceTempView("people")
    spark.sql("select * from people").show()

    df.createGlobalTempView("peoples")
    spark.sql("select * from global_temp.peoples ").show()

    spark.newSession().sql("SELECT * FROM global_temp.peoples").show()
  }

  private def runInferSchemaExample(spark: SparkSession): Unit = {
    import spark.implicits._
    val peopleDF = spark.sparkContext.textFile("src/main/resources/people.txt")
      .map(_.split(","))
      .map(p => Person(p(0), p(1).trim.toInt))
      .toDF()
    peopleDF.show();
    peopleDF.createOrReplaceTempView("people")

    val result = spark.sql("select * from people where age between 13 and 19")
    result.show()

    result.map(t => "Name:" + t(0)).show()

    result.map(teenager => "Name: " + teenager.getAs[String]("name")).show()

    implicit val mapEncoder = org.apache.spark.sql.Encoders.kryo[Map[String, Any]]

    result.map(t => t.getValuesMap[Any](List("name", "age"))).collect()
  }

  private def runProgrammaticSchemaExample(spark: SparkSession): Unit = {

    val peopleRDD = spark.sparkContext.textFile("src/main/resources/people.txt")
    val schemaString = "name age"

    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(fields)

    val rowRDD = peopleRDD
      .map(_.split(","))
      .map(attributes => Row(attributes(0), attributes(1).trim))

    val peopleDF = spark.createDataFrame(rowRDD, schema)

    peopleDF.createOrReplaceTempView("people")

    val results = spark.sql("SELECT name FROM people")
    results.show()
    import spark.implicits._
    results.map(attributes => "Name: " + attributes(0)).show()

  }
}
