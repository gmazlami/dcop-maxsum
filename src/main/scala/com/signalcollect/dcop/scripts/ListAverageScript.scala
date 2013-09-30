package com.signalcollect.dcop.scripts

import java.io.FileNotFoundException
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

object ListAverageScript extends App {

  var path = "results/conflictsOverTime/SYNC/adopt40scripts"
  val files = listFileNames
  var listList: ListBuffer[ListBuffer[Int]] = ListBuffer()
  var timeList = List(500,1000,1500,2500,5000,7500,10000)
  files.foreach { file =>
    listList += fromEdgeList(file)
  }

  new FileWriter(createTupleList(average(listList , 7)), "scripts/adopt40_MaxSumSync.txt").write
  
  
  private def createTupleList(list : ListBuffer[Double]) = {
    var results : ListBuffer[Tuple2[Int,Double]] = ListBuffer.fill(list.size)(null)
    for(i <- 0 to list.size - 1){
      results(i) = (timeList(i), list(i))
    }
  	results
  }
  
  private def average(list: ListBuffer[ListBuffer[Int]], size : Int) : ListBuffer[Double] = {
   val temp : ListBuffer[Double] = ListBuffer.fill(size)(0.0)
   for(inner <- 0 to size - 1){
    for(l <- list){
    	  temp(inner) += l(inner)
      }
    }
  	var results : ListBuffer[Double] = ListBuffer.fill(size)(0.0)
  	for(i <- 0 to temp.size - 1){
  	  val t = temp(i)
  	  results(i) = (t / list.size) 
  	}
  	results
  }

  private def fromEdgeList(filename: String): ListBuffer[Int] = {
    var list: ListBuffer[Int] = ListBuffer()
    val src = io.Source.fromFile(filename)

    src.getLines foreach { line =>
      val Array(iStr, jStr) = line.split(" ")
      list += jStr.toInt
    }

    list
  }

  private def listFileNames(): ListBuffer[String] = {
    var list: ListBuffer[String] = ListBuffer()
    (new java.io.File(path)).listFiles.foreach { file =>
      if (file.isFile()) {
        list += file.getPath()
      }
    }
    list
  }
}