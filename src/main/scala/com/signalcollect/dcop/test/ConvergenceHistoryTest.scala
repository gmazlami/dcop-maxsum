package com.signalcollect.dcop.test

import com.signalcollect.dcop.termination.ConvergenceHistory

object ConvergenceHistoryTest extends App {

  
  val h : ConvergenceHistory[Int] = new ConvergenceHistory(3)
  
  h.push(5)
  h.push(12)
  h.push(3)
  h.push(45)
  
  println(h)
  
  println(h.isFull)
  
  h.push(10)
  
  println(h)
}