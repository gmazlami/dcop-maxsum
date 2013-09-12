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


import com.signalcollect.configuration.ExecutionMode


trait AdjustmentSchedule {
  val underlyingSignalCollectSchedule: ExecutionMode.Value

  def shouldComputeNewState: Boolean
}


trait FloodSchedule extends AdjustmentSchedule {
  override val underlyingSignalCollectSchedule = ExecutionMode.Synchronous

  override def shouldComputeNewState = true
}


trait ParallelRandomSchedule extends AdjustmentSchedule {
  val rand = scala.util.Random
  val p: Double

  override val underlyingSignalCollectSchedule = ExecutionMode.Synchronous

  override def shouldComputeNewState = {
    if (rand.nextDouble() <= p) true
    else false
  }
}


trait AsyncRandomSchedule extends AdjustmentSchedule {
  val rand = scala.util.Random
  val p: Double

  override val underlyingSignalCollectSchedule = ExecutionMode.PureAsynchronous

  override def shouldComputeNewState = {
    if (rand.nextDouble() <= p) true
    else false
  }
}


// == sync
trait CompletelyParallelSchedule extends ParallelRandomSchedule {
  override val p = 1.0
}


trait SequentialRandomSchedule extends AdjustmentSchedule {
  var stepCounter: Int
  val rand = scala.util.Random
  val id: Int // the vertex' id
  val vertexIds: Seq[Int] // all the ids of all vertices in the graph

  override val underlyingSignalCollectSchedule = ExecutionMode.Synchronous

  override def shouldComputeNewState = {
    // set the seed on the random number generator
    // so that all vertices will get the same 'random' number
    rand.setSeed(stepCounter)
    val randIdx = rand.nextInt(vertexIds.size)
    val vertexId = vertexIds(randIdx)
    if (id == vertexId) true
    else false
  }
}

