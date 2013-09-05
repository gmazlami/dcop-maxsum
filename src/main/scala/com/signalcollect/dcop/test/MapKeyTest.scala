package com.signalcollect.dcop.test

import scala.collection.mutable.HashMap
import com.signalcollect.dcop.vertices.id.MaxSumId

object MapKeyTest extends App {

  val map : HashMap[MaxSumId,String] = HashMap()
  
  //fill in data
  var id1 = new MaxSumId(1,0)
  var id2 = new MaxSumId(1,0)
  map += (id1 -> "Node1")
  map += (new MaxSumId(2,0) -> "Node2")
  map += (new MaxSumId(1,1) -> "Node3")
  map += (new MaxSumId(2,1) -> "Node4")
  
  //try to get data:
  println(map(id1))
  println(map.apply(id2))
  
  
  
  
}