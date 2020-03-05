//package com.maomao.sparkwork
//
//import org.apache.hadoop.hbase.client.Scan
//import org.apache.hadoop.hbase.{HBaseConfiguration, HConstants, TableName}
//import org.apache.hadoop.hbase.mapreduce.TableInputFormat
//import org.apache.hadoop.hbase.spark.HBaseContext
//import org.apache.hadoop.hbase.util.Bytes
//import org.apache.spark.{SparkConf, SparkContext}
//
//object SparkOnHbase {
//  def main(args: Array[String]): Unit = {
//    val sparkConf = new SparkConf().setMaster("local").setAppName("sparkonhbase")
//    val sc = new SparkContext(sparkConf)
//    val hconf = HBaseConfiguration.create()
//    val zooKeeper = "127.0.0.1:2181"
//    hconf.set(HConstants.ZOOKEEPER_QUORUM, zooKeeper)
//
//    val scan = new Scan()
//    scan.setStartRow(Bytes.toBytes("COHUTTA 3/10/14"))
//    scan.setStopRow(Bytes.toBytes("COHUTTA 3/11/14"))
//    val hBaseContext = new HBaseContext(sc, hconf)
//    val hbaseRDD = hBaseContext.hbaseRDD(TableName.valueOf("tablename"), scan)
//    hbaseRDD.count()
//    hbaseRDD.foreach {
//      case (_, result) => {
//        //获取行键
//        val key = Bytes.toString(result.getRow)
//        //通过列族和列名获取列
//        val citycode = Bytes.toString(result.getValue("f1".getBytes, "citycode".getBytes))
//        val daytime = Bytes.toInt(result.getValue("f1".getBytes, "daytime".getBytes))
//        println("Row key:" + key + " Name:" + citycode + " Age:" + daytime)
//      }
//    }
//
//  }
//}
