package com.maomao.mlib

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.{Matrices, Matrix, Vectors}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD

/**
 * @Author maohongqi
 * @Date 2020/1/19 15:46
 * @Version 1.0
 **/
object Test {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local")
    val sc = SparkContext.getOrCreate(conf)
    //创建密集向量(1.0, 0.0, 3.0)
    val dv: Vector = Vectors.dense(1.0, 0.0, 3.0)
    //给向量(1.0, 0.0, 3.0)创建疏向量
    val svl: Vector = Vectors.sparse(3, Array(0, 2), Array(1.0, 3.0))
    //通过指定非0的项目，创建稀疏向量(1.0, 0.0, 3.0)
    val sv2: Vector = Vectors.sparse(3, Seq((0, 1.0), (2, 3.0)))


    //使用标签1.0和一个密集向量创建一个标记点
    val pos = LabeledPoint(1.0, Vectors.dense(1.0, 0.0, 3.0))
    //使用标签0.0和一个疏向量创建一个标记点
    val neg = LabeledPoint(0.0, Vectors.sparse(3, Array(0, 2), Array(1.0, 3.0)))


    val examples: RDD[LabeledPoint] = MLUtils.loadLibSVMFile(sc, "data/mllib/sample_libsvm_data.txt")


    //创建密矩阵（（1.0，2.0），（3.0, 4.0），（5.0, 6.0））
    val dm: Matrix = Matrices.dense(3, 2, Array(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))
  }


}
