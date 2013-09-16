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
	val stepA = a.head._1
	val valA = a.head._2
	
	val stepB = b.head._1
	val valB = b.head._2
	
	if(stepA == stepB){
	  val res = valA + valB
	  Map(stepA -> res)
	}else{
	  var map = Map(stepA -> valA)
	  map = map + (stepB -> valB)
	  map
	}
  }

  /**
   * Reduces an arbitrary number of elements to one element.
   */
  override def reduce(elements: Stream[Map[Int,Int]]): Map[Int,Int] = {
    //TODO: I have no idea what the reduce step does
    var aggregation : Map[Int,Int] = Map()
    		
    for(i <- 0 to elements.size - 2){
      aggregation = aggregate(elements(i), elements(i+1))
    }

    aggregation
  }

  /**
   *  Neutral element of the `aggregate` function:
   *  `aggregate(x, neutralElement) == x`
   */
  val neutralElement: Map[Int,Int] = Map()

}