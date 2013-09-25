package com.signalcollect.dcop.scripts

import scala.collection.mutable.ListBuffer
import java.io.PrintWriter
import java.io.File

class FileWriter(list : ListBuffer[Tuple2[Int,Double]], path : String) {

  val newLine = "\n"
  val writer = new PrintWriter(new File(path))
  
  def write() = {
    list.foreach{ l =>
      writer.write(l._1.toString)
      writer.write(" ")
      writer.write(l._2.toString)
      writer.write(newLine)
    }
    writer.close()
  }
}