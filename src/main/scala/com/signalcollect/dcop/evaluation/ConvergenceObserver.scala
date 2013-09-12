package com.signalcollect.dcop.evaluation

import scala.collection.mutable.HashMap
import com.signalcollect.dcop.vertices.id.MaxSumId

object ConvergenceObserver {

  //this map stores the vertices as keys with a boolean as a value stating wether the messages at that vertex have converged
  var convergedVertices : HashMap[MaxSumId,Boolean] = HashMap()
}