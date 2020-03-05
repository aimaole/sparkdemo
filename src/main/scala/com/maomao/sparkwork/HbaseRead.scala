package com.maomao.sparkwork

import org.apache.spark.sql.SparkSession
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.hadoop.hbase.protobuf.ProtobufUtil

object HbaseRead {
  def main(args: Array[String]): Unit = {
    val sess = SparkSession.builder().appName("wangjk").master("local[2]")
      .config("spark.testing.memory", "2147480000").getOrCreate();
    val sc = sess.sparkContext;

    val tablename = "Air:airDay"
    val conf = HBaseConfiguration.create()
    //设置zooKeeper集群地址，也可以通过将hbase-site.xml导入classpath，但是建议在程序里这样设置
    conf.set("hbase.zookeeper.quorum", "192.168.0.112:2181,192.168.0.114:2181,192.168.0.116:2181")
    //设置zookeeper连接端口，默认2181
    conf.set("hbase.zookeeper.property.clientPort", "2181")
    conf.set(TableInputFormat.INPUT_TABLE, tablename)


    val startRowkey = "0,110000,20180220"
    val endRowkey = "0,110000,20180302"
    //开始rowkey和结束一样代表精确查询某条数据

    //组装scan语句
    val scan = new Scan(Bytes.toBytes(startRowkey), Bytes.toBytes(endRowkey))
    scan.setCacheBlocks(false)
    /*    scan.addFamily(Bytes.toBytes("ks"));
        scan.addColumn(Bytes.toBytes("ks"), Bytes.toBytes("data"))*/

    //将scan类转化成string类型
    val proto = ProtobufUtil.toScan(scan)
    val ScanToString = Base64.encodeBytes(proto.toByteArray());
    conf.set(TableInputFormat.SCAN, ScanToString)

    //读取数据并转化成rdd
    val hBaseRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])

    val count = hBaseRDD.count()
    println(count)
    hBaseRDD.foreach { case (_, result) => {
      //获取行键
      val key = Bytes.toString(result.getRow)
      //通过列族和列名获取列
      val citycode = Bytes.toString(result.getValue("f1".getBytes, "citycode".getBytes))
      val daytime = Bytes.toInt(result.getValue("f1".getBytes, "daytime".getBytes))
      println("Row key:" + key + " Name:" + citycode + " Age:" + daytime)
    }
    }


  }

}
