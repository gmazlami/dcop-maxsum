package com.signalcollect.dcop.util

import scala.collection.mutable.HashMap
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.mutable.ArrayBuffer

object ProblemConstants {

  var colors : Set[Int] = Set()
  
  var numOfColors : Int = 0
  
  var initialPreferences : HashMap[MaxSumId,ArrayBuffer[Double]] = HashMap()
  
  var neighborStructure : HashMap[MaxSumId,Set[MaxSumId]] = HashMap()

}