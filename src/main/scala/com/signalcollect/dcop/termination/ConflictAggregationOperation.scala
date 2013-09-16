package com.signalcollect.dcop.termination

import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.Vertex
import com.signalcollect.dcop.vertices.MaxSumVertex

class ConflictAggregationOperation extends AggregationOperation[Int] {

    /**
   *  Extracts values of type `ValueType` from vertices.
   */
  def extract(v: Vertex[_, _]): Int = {
    val vertex = v.asInstanceOf[MaxSumVertex]
    vertex.getNumOfConflicts
  }

  /**
   *  Aggregates all the values extracted by the `extract` function.
   *
   *  @note There is no guarantee about the order in which this function gets executed on the extracted values.
   */
  def aggregate(a: Int, b: Int): Int = {
    a + b
  }

   /**
   * Reduces an arbitrary number of elements to one element.
   */
  override def reduce(elements: Stream[Int]): Int = {
    var sum = 0
    elements.foreach{el =>
      sum += el
    }
    sum //TODO: don't know hat "reduce" will be used for
  }
  
  /**
   *  Neutral element of the `aggregate` function:
   *  `aggregate(x, neutralElement) == x`
   */
  val neutralElement: Int = 0
  
  
}