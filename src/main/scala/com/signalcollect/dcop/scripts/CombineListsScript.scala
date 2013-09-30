package com.signalcollect.dcop.scripts

import scala.collection.mutable.ListBuffer
import java.io.PrintWriter
import java.io.File

object CombineListsScript extends App {

  var pathString = "scripts"
  val files = listFileNames
  var listList: ListBuffer[ListBuffer[Double]] = ListBuffer()

  var timeList = List(500,1000,1500,2500,5000,7500,10000)
  
  files.foreach { file =>
    listList += fromEdgeList(file)
  }

  write(listList , 7, "scripts/stepstime.txt")

  private def fromEdgeList(filename: String): ListBuffer[Double] = {
    var list: ListBuffer[Double] = ListBuffer()
    val src = io.Source.fromFile(filename)

    src.getLines foreach { line =>
      val Array(iStr, jStr) = line.split(" ")
      list += jStr.toDouble
    }

    list
  }

  private def listFileNames(): ListBuffer[String] = {
    var list: ListBuffer[String] = ListBuffer()
    (new java.io.File(pathString)).listFiles.foreach { file =>
      if (file.isFile()) {
        list += file.getPath()
      }
    }
    println(list)
    list
  }
  
  private def write(list : ListBuffer[ListBuffer[Double]], size : Int, path :String) = {
    
    var stringList : ListBuffer[String] = ListBuffer.fill(size)(null)
    		
    for(i <- 0 to size - 1){
      stringList(i) = (timeList(i).toString + " ")
    }
    
    for(i <- 0 to size -1){
      for(j <- 0 to list.size - 1){
    	  stringList(i) += ((list(j)(i)).toString + " ")
      }
    }
  	val writer = new PrintWriter(new File(path))
  	
  	stringList.foreach{ el =>
  	  writer.write(el.toString())
  	  writer.write("\n")
  	}
  	writer.close()
  }
  
}