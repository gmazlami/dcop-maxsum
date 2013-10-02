/*
 *  @author Robin Hafen and Genc Mazlami
 *
 *  Copyright 2013 University of Zurich
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.signalcollect.dcop.graphs

import com.signalcollect.dcop.evaluation.candidates.ConstraintGraphProvider
import java.io.PrintWriter
import com.signalcollect.Vertex
import scala.collection.mutable.LinkedHashSet
import java.io.File
import com.signalcollect.dcop.evaluation.candidates.VertexBuilder
import com.signalcollect.GraphEditor

class GraphGenerator(
    val graphSize: Int,
    val meanDegree: Double,
    val domainSize: Int,
    val saveTo: String){

  type Degree = Double

  def generateToFile() = {
    toFile(computeEdgeList()._1, saveTo)
  }
  
  // To speed up the pruning process, this function defines how many edges should 
  // be removed at each prune step.
  private def numberToPrunePerStep(currentAvgDegree: Double) = {
    if (currentAvgDegree < meanDegree + 20) {
      1
    } else {
      graphSize
    }
  }

  private def toFile(undirectedEdges: LinkedHashSet[Set[Int]], filename: String) {
	  val writer = new PrintWriter(new File(filename))
	  undirectedEdges.foreach{edge =>
	    var string = ""
	    edge.foreach{v =>
	      string += (" " + v.toString())
	    }  
	    writer.write(string)
	    writer.write("\n")
	  }
	  writer.close()
  }
  
  /*
   * Prune a fully connected graph until it has a mean degree according to the one
   * specified in the constructor.
   * The actual degree may vary slightly and thus is returned as well.
   */
  private def computeEdgeList(): (LinkedHashSet[Set[Int]], Degree) = {
    import scala.util.Random

    var vertexList: List[Vertex[Any,_]] = List()

    val rand = Random

    val is = 0 until graphSize toList

    // A fully connected graph represented by a list of sets containing two vertex ids {i, j}
    val conn: List[Set[Int]] =
      List.fill(2)(is).flatten.combinations(2)
                      .map { xs => Set(xs(0), xs(1)) }
                      .filterNot { _.size == 1 }
                      .toList

    // Convert it to an ordered mutable Hash set
    val undirectedEdges = LinkedHashSet[Set[Int]]( conn:_* )

    def computeAvgDegree(is: Iterable[Int], edges: LinkedHashSet[Set[Int]]): Double = {
      val degs = is map { i =>
        edges count { edge => edge contains i }
      }
      degs.sum / is.size.toDouble
    }

    def removeRandomEdge(edges: LinkedHashSet[Set[Int]]) {
      val elem = rand.shuffle(undirectedEdges).head
      val List(i, j) = elem.toList
      val otherEdges = undirectedEdges filter { _ != elem }
      val notLastEdgeI = otherEdges exists { edge => edge(i) }
      val notLastEdgeJ = otherEdges exists { edge => edge(j) }
      if (notLastEdgeI && notLastEdgeJ) {
        edges.remove(elem)
      } else {
        throw new Exception("This is the last edge for one of the involved vertices!")
      }
    }

    var keepRemoving = true
    var currentAvgDegree = -1.0
    while (keepRemoving) {
      currentAvgDegree = computeAvgDegree(is, undirectedEdges)
      try {
        (1 to numberToPrunePerStep(currentAvgDegree)) foreach { _ =>
          removeRandomEdge(undirectedEdges)
        }
      } catch {
        case e: Throwable => ()//println("[warning] trying to remove last edge for a vertex")
      }
      if (currentAvgDegree < meanDegree) {
        keepRemoving = false
        println("[info] Graph built with final average degree: " + currentAvgDegree)
      }
    }

    (undirectedEdges, currentAvgDegree)
  }
}
