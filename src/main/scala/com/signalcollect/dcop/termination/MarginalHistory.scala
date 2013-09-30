package com.signalcollect.dcop.termination

import scala.math.abs
import scala.collection.mutable.ArrayBuffer

class MarginalHistory[T](c: Int, epsilon: Double) extends ConvergenceHistory[ArrayBuffer[Double]](c) {

  val e = epsilon

  override def hasConverged() = {
    if (!isFull) {
      false
    } else {
      var converged = true
      val list = this.toList
      var i = 0
      while ((i < list.size - 2) && converged) {
        if (compare(list(i), list(i + 1))) {
          converged = false
        }
        i += 1
      }
      converged
    }
  }

  private def compare(a: ArrayBuffer[Double], b: ArrayBuffer[Double]) = {
    var equal: Boolean = true
    var i = 0
    while (i < a.length && equal) {
      if (abs(a(i) - b(i)) > e) {
        equal = false
      }
      i += 1
    }
    equal
  }

}