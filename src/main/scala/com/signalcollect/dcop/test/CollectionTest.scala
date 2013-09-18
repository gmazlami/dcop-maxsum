package com.signalcollect.dcop.test

object CollectionTest extends App {

  
  var list : List[Tuple2[Int,Int]] = List()
  
  
  val tuple1 = (1,1)
  val tuple2 = (2,3)
  val tuple3 = (3,6)
  val tuple4 = (4,10)
  val tuple5 = (5,0)
  
  list :+= tuple1
  list :+= tuple2
  list :+= tuple3
  list :+= tuple4
  list :+= tuple5
  
  println(list)
}