package com.signalcollect.dcop.test

import com.signalcollect.dcop.vertices.id.MaxSumId

object EqualityTest extends App {

  val id1 : MaxSumId = new MaxSumId(1,1)
  
  val id2 : MaxSumId = new MaxSumId(1,0)
  
  val id3 : MaxSumId = new MaxSumId(1,1)
  
  if(id1 == id2){
    println("WRONG! id2 should not be equal to id1")
  }else{
    println("CORRECT: id1 != id2")
  }
  
  if(id1 == id3){
    println("CORRECT: id1 == id3")
  }else{
    println("WRONG! id3 should be equal to id1")
  }
  
}