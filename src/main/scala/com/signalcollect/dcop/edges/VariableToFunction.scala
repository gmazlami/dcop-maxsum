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

package com.signalcollect.dcop.edges

import com.signalcollect.DefaultEdge
import com.signalcollect.dcop.vertices.VariableVertex
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.dcop.util.ProblemConstants
import com.signalcollect.dcop.MaxSumMessage

/**
 * Edge type that computes messages from VariableVertex instances to FunctionVertex instances.
 * Computes messages Q_m_n according to specification of the Max-Sum algorithm in Farinelli's Paper: 
 * Decentralised coordination of low-power embedded devices using the max-sum algorithm.
 */
class VariableToFunction(id: MaxSumId) extends DefaultEdge(id) {

  type Source = VariableVertex

  //the signal that is computed by this edge
  def signal = Q_n_m

  def Q_n_m = {

    //the variables from which the received messages will be summed to compute the new message
    val variableIdSet = source.getNeighborIds - (targetId.asInstanceOf[MaxSumId])

    //the normalization factor (as in Farinelli's Paper: Decentralized Coordination of..... (2008))
    val alpha_n_m = computeNormalizationFactor(variableIdSet)

    //initialize new message to 0.0 
    var resultMessage: ArrayBuffer[Double] = ArrayBuffer.fill(ProblemConstants.numOfColors)(0.0)

    //sum up the received messages component-wise
    variableIdSet.foreach { variableId =>
      val message = source.receivedMessages(variableId).value
      if (message.length != ProblemConstants.numOfColors) { //this is not allowed to happen
        println("FATAL: message length not equal to number of possible colors! Aborting..")
        System.exit(-1)
      } else {
        for (i <- 0 to resultMessage.length - 1) {
          resultMessage(i) = resultMessage(i) + message(i)
        }
      }
    }
    
    // normalize the values to avoid indefinitely increasing preferences
    for (i <- 0 to resultMessage.length - 1) {
      resultMessage(i) = alpha_n_m + resultMessage(i)
    }

    //the resulting message of this edge
    new MaxSumMessage(source.id, targetId, resultMessage)
  }

  /*
   * computes the normalization factor
   */
  private def computeNormalizationFactor(neighborIds: ArrayBuffer[MaxSumId]): Double = {
    var sum = 0.0
    val v = ProblemConstants.numOfColors
    for (color <- 0 to v - 1) {
      neighborIds.foreach { nId =>
        val message = source.receivedMessages(nId).value
        sum += message(color)
      }
    }
    sum = sum / v
    sum = 0 - sum
    sum
  }

}