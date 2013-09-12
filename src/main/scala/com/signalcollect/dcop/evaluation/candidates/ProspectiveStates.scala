/*
 *  @author Robin Hafen
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


package com.signalcollect.dcop.evaluation.candidates

import scala.util.Random


/**
 * The basic type of a ProspectiveStates-Mixin
 * Its function draws a subset of values from each vertex' domain,
 * so that the target function can evaluate it.
 * @tparam State A vertex' state
 */
trait ProspectiveStates[State] {
  def prospectiveStates(allStates: Set[State]): Seq[State]
}


/**
 * Yield all values in the domain WITHOUT the current one and randomize the results.
 * @tparam State A vertex' state
 */
trait CompleteSearch[State] extends ProspectiveStates[State] {
  var state: State
  def prospectiveStates(allStates: Set[State]): Seq[State] = {
    val searchSpace = allStates - state
    val rand = new Random
    rand.shuffle(searchSpace.toSeq)
  }
}


/**
 * Randomly choose a single state from the domain.
 * NOTE: This can be the same as the current one.
 * @tparam State A vertex' state
 */
trait RandomStateSearch[State] extends ProspectiveStates[State] {
  def prospectiveStates(allStates: Set[State]): Seq[State] = {
    val rand = scala.util.Random
    val states = allStates.toSeq
    Seq(states(rand.nextInt(states.size)))
  }
}


/**
 * Randomly choose values from the domain (can't be equal) until a
 * certain number is reached. If the subset size is bigger than the size of the domain,
 * the value just gets limited to the domain's size.
 * @tparam State A vertex' state
 */
trait SubsetSearch[State] extends ProspectiveStates[State] {
  val subsetSize: Int
  def prospectiveStates(allStates: Set[State]): Seq[State] = {
    val actualSize = if (subsetSize > allStates.size) allStates.size else subsetSize
    val rand = scala.util.Random
    var statesToSearch = allStates.toSeq
    var chosenStates = Set[State]()
    while (chosenStates.size != actualSize) { {
        val randomState = statesToSearch(rand.nextInt(statesToSearch.size))
        chosenStates += randomState
        statesToSearch = statesToSearch filter (_ != randomState)
      }
    }
    chosenStates.toSeq
  }
}
