package com.maomao.sparkwork

import org.apache.spark.sql.SparkSession
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.hbase.io.ImmutableBytesWritable

object HbaseWrite {
  def main(args: Array[String]): Unit = {
    val sess = SparkSession.builder().appName("wangjk").master("local[2]")
      .config("spark.testing.memory", "2147480000").getOrCreate();
    val sc = sess.sparkContext;

    val tablename = "default:airTest"
    val conf = HBaseConfiguration.create()
    //设置zooKeeper集群地址，也可以通过将hbase-site.xml导入classpath，但是建议在程序里这样设置
    conf.set("hbase.zookeeper.quorum", "192.168.0.112:2181,192.168.0.114:2181,192.168.0.116:2181")
    //设置zookeeper连接端口，默认2181
    conf.set("hbase.zookeeper.property.clientPort", "2181")

    //初始化jobconf，TableOutputFormat必须是org.apache.hadoop.hbase.mapred包下的！
    val jobConf = new JobConf(conf)
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, tablename)

    val indataRDD = sc.makeRDD(Array("1,jack,15", "2,Lily,16", "3,mike,16"))


    val rdd = indataRDD.map(_.split(',')).map { arr => {
      /*一个Put对象就是一行记录，在构造方法中指定主键
       * 所有插入的数据必须用org.apache.hadoop.hbase.util.Bytes.toBytes方法转换
       * Put.add方法接收三个参数：列族，列名，数据
       */
      val put = new Put(Bytes.toBytes(arr(0).toInt))
      put.add(Bytes.toBytes("f1"), Bytes.toBytes("name"), Bytes.toBytes(arr(1)))
      put.add(Bytes.toBytes("f1"), Bytes.toBytes("age"), Bytes.toBytes(arr(2).toInt))
      //转化成RDD[(ImmutableBytesWritable,Put)]类型才能调用saveAsHadoopDataset
      (new ImmutableBytesWritable, put)
    }
    }

    rdd.saveAsHadoopDataset(jobConf)


  }
}
