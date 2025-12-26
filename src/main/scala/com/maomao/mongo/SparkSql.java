package com.maomao.mongo;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkSql {
    public static void main(String[] args) throws AnalysisException {
        String mongoUrl = "mongodb://root:123456@127.0.0.1:12345/";
        String database = "test";
        String dbCollection = "nosrc";
        String outCollection = "nosrc1";


//        SparkSession spark = SparkSession.builder()
//                .master("local")
//                .appName("mongotest")
//                .config("spark.mongodb.read.connection.uri", mongoUrl + database + "." + dbCollection + "?authSource=admin")
//                .config("spark.mongodb.write.connection.uri",  mongoUrl + database + "." + outCollection + "?authSource=admin")
//                .getOrCreate();
//
//        Dataset<Row> dataset = spark.read().format("mongodb").load();
//
//        dataset.show();
//        dataset.createTempView("table");
//
//        dataset.write().format("mongodb").mode("overwrite").save();
//
//        Dataset<Row> sql = spark.sql("select menzhenjianyanbaogao.lab_report.department from table");
//        sql.show();
//        spark.stop();


        SparkSession spark = SparkSession.builder()
                .master("local[4]")
                .appName("mongotest")
                .getOrCreate();

        Dataset<Row> ds = spark.read().format("mongodb")
                .option("spark.mongodb.read.connection.uri", mongoUrl + database + "." + dbCollection + "?authSource=admin").load();

        ds.createTempView("table");

        spark.sql("select explode(table.field) as field from table ").createTempView("table1");
        Dataset<Row> sql = spark.sql("select field, count(field) from table1 group by field");

        sql.show();
        spark.stop();


    }
}
