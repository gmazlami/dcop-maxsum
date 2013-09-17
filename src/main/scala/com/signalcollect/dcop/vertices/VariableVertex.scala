/*
 *  @author Genc Mazlami
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
 *
 */

package com.signalcollect.dcop.vertices

import com.signalcollect.DataGraphVertex
import com.signalcollect.dcop.MaxSumMessage
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.util.ProblemConstants
import com.signalcollect.dcop.evaluation.statistics.ConvergenceObserver
import com.signalcollect.dcop.evaluation.statistics.GlobalMeasurer

class VariableVertex(id: MaxSumId, state: Int) extends MaxSumVertex(id, state) {

  type Signal = MaxSumMessage

  var marginal: ArrayBuffer[Double] = ArrayBuffer.fill(ProblemConstants.numOfColors)(0.0)

  var lastColor: Int = -1

  var stepCounter = 0

  var currentColor: Int = -1

  def collect = {
    mostRecentSignalMap.foreach { mapEntry =>
      val currentId = mapEntry._1.asInstanceOf[MaxSumId]
      val message = mapEntry._2.asInstanceOf[MaxSumMessage]
      checkMessageConvergence(message)
      receivedMessages += (currentId -> message)
    }

    //compute updated marginal with new received messages
    marginal = ArrayBuffer.fill(ProblemConstants.numOfColors)(0.0)
    for (color <- 0 to ProblemConstants.numOfColors - 1) {
      receivedMessages.values.foreach { message =>
        marginal(color) += message.value(color)
      }

    }
    stepCounter += 1
    currentColor = findResultingColorFromMarginal
    ProblemConstants.setColorToVariableVertex(id, currentColor)
    //record current number of conflicts at this time step (for synchronous mode)
    GlobalMeasurer.maxsumInstrument.updateConflictsOverTime(stepCounter)
    currentColor
  }

  private def findResultingColorFromMarginal(): Int = {
    var max = Double.MinValue
    var maxColor = -1
    for (color <- 0 to ProblemConstants.numOfColors - 1) {
      val value = marginal(color)
      if (max < value) {
        max = value
        maxColor = color
      }
    }
    maxColor
  }

  private def checkStateConvergence(newColor: Int) = {
    if (lastColor == -1) {
      lastColor = newColor
    } else {
      if (lastColor == newColor) {
        ConvergenceObserver.stateConvergedVertices += (id -> true)
      } else {
        lastColor = newColor
      }
    }
  }

  override def getNumOfConflicts(): Int = {
    var conflicts = 0

    val neighbors = getNeighborVertices
    neighbors.foreach { n =>
      val vertex = n.asInstanceOf[VariableVertex]
      if (vertex.id != id.asInstanceOf[MaxSumId]) {
        println("config:  " + id.id + "=" + currentColor + " -VS- " + vertex.id.id + "=" + vertex.currentColor)
        if (currentColor == vertex.currentColor) {
          conflicts += 1
        }
      }
    }
    println("GetNumOfConflicts for " + id.id + " = " + conflicts)
    println
    conflicts

  }

  override def getConflictsAndStep(): Map[Int, Int] = {
    Map(stepCounter -> getNumOfConflicts)
  }

  private def findColorForPref() = {
    var max = Double.MinValue
    var maxColor = -1
    val array = ProblemConstants.initialPreferences(id.asInstanceOf[MaxSumId])
    for (color <- 0 to ProblemConstants.numOfColors - 1) {
      val value = array(color)
      if (value > max) {
        max = value
        maxColor = color
      }
    }
    maxColor
  }

  override def scoreSignal: Double = {
//    val con = getNumOfConflicts
//    if (con > 0) {
//      println(id.id + " SIGNALS with conflicts " + con)
//      1
//    } else {
//      0
//    }
    1
  }

  override def scoreCollect: Double = {
//    val con = getNumOfConflicts
//    if (con > 0) {
//      println(id.id + " COLLECTS with conflicts " + con)
//      1
//    } else {
//      0
//    }
    1
  }

}