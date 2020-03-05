package com.maomao.examples

import org.apache.spark.sql.SparkSession

object SparkPi {
  def main(args: Array[String]) {
//    val conf = new SparkConf().setAppName("Spark Pi").setMaster("local[2]")
    val spark = SparkSession.builder().appName("sparkPi").master("local[4]").getOrCreate()

    val slices = 100;
    val n = 1000 * slices //选n个点
    val count = spark.sparkContext.parallelize(1 to n, slices).map({ i =>

      /** Returns a `double` value with a positive sign, greater than or equal
        * to `0.0` and less than `1.0`.
        */
      def random: Double = java.lang.Math.random()

      //这里取圆心为坐标轴原点，在正方向中不断的随机选点
      val x = random * 2 - 1
      val y = random * 2 - 1
      println(x + "--" + y)
      //通过在圆内的点
      if (x * x + y * y < 1) 1 else 0

    }).reduce(_ + _)

    //pi=S2=S1*count/n
    println("Pi is roughly " + 4.0 * count / n)


    spark.stop()
  }

}
