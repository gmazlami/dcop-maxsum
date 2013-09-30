package com.signalcollect.dcop.test

import scala.collection.mutable.Queue

object QueueTest extends App {

  val q : Queue[Int] = Queue()
  
  q.enqueue(2)
  q.enqueue(3)
  q.enqueue(12)
  
  println(q.dequeue)
  println(q)
}