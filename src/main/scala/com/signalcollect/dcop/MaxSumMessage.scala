package com.signalcollect.dcop

import com.signalcollect.dcop.vertices.id.MaxSumId

class MaxSumMessage(s : MaxSumId, v : Array[Double]) {

  val source : MaxSumId = s
  
  val value : Array[Double] = v
  
}