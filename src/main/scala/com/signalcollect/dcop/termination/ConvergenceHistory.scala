package com.signalcollect.dcop.termination

import scala.collection.mutable.Queue

class ConvergenceHistory[T](c: Int) extends Queue[T] {

  val capacity = c

  def push(element: T): Unit = {
    if (size < capacity) {
      enqueue(element)
    } else {
      dequeue
      enqueue(element)
    }
  }

  def isFull(): Boolean = {
    if (size < capacity) {
      false
    } else {
      true
    }
  }

  def hasConverged(): Boolean = {
    if (!isFull) {
      false
    } else {
      var converged = true
      val list = this.toList
      var i = 0
      while ((i < list.size - 2) && converged) {
        if (list(i) != list(i + 1)) {
          converged = false
        }
        i += 1
      }
      converged
    }
  }

  override def toString = {
    var string = ""
    val list = this.toList
    for (l <- list) {
      string += (" " + l.toString)
    }
    string
  }

}