package com.signalcollect.dcop.util

import scala.collection.mutable.HashMap
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.mutable.ArrayBuffer

object ProblemConstants {

  var colors: Set[Int] = Set()

  var numOfColors: Int = 0

  var initialPreferences: HashMap[MaxSumId, ArrayBuffer[Double]] = HashMap()

  var neighborStructure: HashMap[MaxSumId, ArrayBuffer[MaxSumId]] = HashMap()

  def getPreferenceTable(variableNode: MaxSumId): ArrayBuffer[Tuple3[MaxSumId, Int, Double]] = {
    println("prefTable: ")
    initialPreferences.keys.foreach{ pref =>
  		println(pref.id)
    }
    
    println("VariableNode: " + variableNode.id)
    
    val preferenceTable: ArrayBuffer[Tuple3[MaxSumId, Int, Double]] = ArrayBuffer.fill(numOfColors)(null)
    val preference = initialPreferences(variableNode)
    for (i <- 0 to preference.length - 1) {
      preferenceTable(i) = (variableNode, i, preference(i))
    }
    preferenceTable
  }

  def getOwnedVariable(functionNode: MaxSumId) = {
    val neighbor = neighborStructure(functionNode)
    neighbor.find{ neighbor => 
      neighbor.idNumber == functionNode.idNumber 
    }.get
  }


}