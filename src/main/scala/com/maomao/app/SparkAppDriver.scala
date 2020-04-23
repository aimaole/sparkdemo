package com.maomao.app

import com.maomao.sparkwork.WordCount
import org.apache.hadoop.util.ProgramDriver

object SparkAppDriver {
  def main(args: Array[String]): Unit = {
    var exitcode = -1
    val pgd = new ProgramDriver
    try {
      pgd.addClass("test", WordCount.getClass, "test WorldCount")
      exitcode = pgd.run(args)
    } catch {
      case e: Exception =>
        e.printStackTrace
    }
    println(exitcode)
  }
}
