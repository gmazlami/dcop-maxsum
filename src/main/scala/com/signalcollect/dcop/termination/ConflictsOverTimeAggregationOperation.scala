package com.signalcollect.dcop.termination

import com.signalcollect.dcop.vertices.MaxSumVertex
import com.signalcollect.Vertex
import com.signalcollect.interfaces.AggregationOperation

class ConflictsOverTimeAggregationOperation extends AggregationOperation[Map[Int,Int]]{

  /**
   *  Extracts values of type `ValueType` from vertices.
   */
  def extract(v: Vertex[_, _]): Map[Int,Int] = {
    val vertex = v.asInstanceOf[MaxSumVertex]
    vertex.getConflictsAndStep
  }

  /**
   *  Aggregates all the values extracted by the `extract` function.
   *
   *  @note There is no guarantee about the order in which this function gets executed on the extracted values.
   */
  def aggregate(a: Map[Int,Int], b: Map[Int,Int]): Map[Int,Int] = {
	var valA = 0
	var stepA = -1
    if(!a.isEmpty){
		stepA = a.head._1
		valA = a.head._2
	}

	var valB = 0
	var stepB = -1
    if(!b.isEmpty){
		stepB = b.head._1
		valB = b.head._2
	}
	
	
	if(stepA == stepB){
	  val res = valA + valB
	  println("stepA = stepB " + Map(stepA -> res))
	  Map(stepA -> res)
	}else{
	  var map = Map(stepA -> valA)
	  map = map + (stepB -> valB)
	  println("stepA != stepB " + map)
	  map
	}
  }

  /**
   * Reduces an arbitrary number of elements to one element.
   */
  override def reduce(elements: Stream[Map[Int,Int]]): Map[Int,Int] = {
    //TODO: I have no idea what the reduce step does
    var aggregation : Map[Int,Int] = Map()
	var temp : Map[Int,Int] = Map()
    for(i <- 0 to elements.size - 2){
      temp = aggregate(elements(i), elements(i+1))
      aggregation = aggregate(temp,aggregation)
    }
  	print("REDUCE: ")
    println(aggregation)
    aggregation
  }

  /**
   *  Neutral element of the `aggregate` function:
   *  `aggregate(x, neutralElement) == x`
   */
  val neutralElement: Map[Int,Int] = Map()

}