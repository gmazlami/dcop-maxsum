
package com.signalcollect.dcop.scripts

import com.signalcollect.dcop.io.FileGraphReader
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.LinkedHashSet
import scala.collection.mutable.HashMap

object GraphConcatenate extends App {

  val reader = new FileGraphReader
  val graph200 = reader.fromEdgeList("graphs/scaling/synthetic/500_3.txt")._1
  val graph300 = reader.fromEdgeList("graphs/scaling/synthetic/graph300.txt")._1
  
//  val tmpList = doPrefix(222)
  
//  val c = concatenate
  
  val c = toListBuffer(graph300)
  c.foreach(ce => println(ce._1 + " " + ce._2))
  println("Nodes: " + countNodes(c))
  
  
  
//  def concatenate()= {
//    var newList : ListBuffer[Tuple2[String,String]] = ListBuffer()
//    newList = toListBuffer(graph300) ++: tmpList
//    newList 
//  }
  
  def doPrefix(prefix : Int) = {
	  var list : ListBuffer[Tuple2[String,String]] = ListBuffer()
	  graph200.foreach{set =>
	    val array : ArrayBuffer[String] = ArrayBuffer.fill(2)("")
	    var index = 0
	    set.foreach{s =>
	      if((index % 2) == 1){
	    	  array(index) = (prefix.toString + s.toString)
	      }else{
	          array(index) = (s.toString)
	      }
	      index += 1
	    }
	    val tuple = (array(0),array(1))  
	    list += tuple
	  }
	  list
  }
  
  def toListBuffer(graph: LinkedHashSet[Set[Int]]) = {
    	  var list : ListBuffer[Tuple2[String,String]] = ListBuffer()
	  graph.foreach{set =>
	    val array : ArrayBuffer[String] = ArrayBuffer.fill(2)("")
	    var index = 0
	    set.foreach{s =>
	      array(index) = (s.toString)
	      index += 1
	    }
	    val tuple = (array(0),array(1))  
	    list += tuple
	  }
	  list
  }
  
  def countNodes(list : ListBuffer[Tuple2[String,String]]) = {
    var map : HashMap[String,Int] = HashMap()
    list.foreach{el =>
      map += (el._1 -> 0)
      map += (el._2 -> 0)
    }
    map.keys.size
  }
}