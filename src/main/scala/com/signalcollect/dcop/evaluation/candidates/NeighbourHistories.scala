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

/**
 * A class to hold information about the states a neighbouring vertex has chosen in its past.
 * FictiousPlay, for example, uses the neighbours' historic frequency of every state in the domain
 * as its target function.
 * TODO: Try faster datastructures (mutable hashmaps)
 */
class NeighbourHistories[State](neighbourIds: Iterable[Any], domain: Array[State]) {
  /* Hold information about a the frequencies of states at a given time */
  case class Record(time: Int, frequencies: Map[State, Double]) {
    def update(newState: State) = {
      val newFrequencies = frequencies map {
        case (st, f) => {
          val i = if (st == newState) 1 else 0 // indicator
          (st, (i + f*time)/(time+1))
        }
      }
      Record(time+1, newFrequencies)
    }
  }

  // Main datastructure for keeping track of which neighbour has which record
  var records: Map[Any, Record] = {
    val pInitial = 1.0/domain.size
    val initialFrequencies = (domain map ((_,pInitial))).toMap
    val initialRecord = Record(domain.size, initialFrequencies)
    neighbourIds map ((_, initialRecord)) toMap
  }

  // Bulk update the record list with a NeighbourId -> newState map
  def updateFrequencies(signalMap: Map[Any, State]) {
    records = signalMap.foldLeft(records) {
      case (recordAccum, (id, newState)) => {
        val updatedRecord = recordAccum(id).update(newState)
        recordAccum.updated(id, updatedRecord)
      }
    }
  }

  def getStateFrequencies(neighbourId: Any): Option[Map[State, Double]] =
    records.get(neighbourId) map { _.frequencies }

  def getStateFrequency(neighbourId: Any, state: State): Option[Double] = {
    getStateFrequencies(neighbourId) flatMap { _.get(state) }
  }

  override def toString = records.map(_.toString).foldLeft("")(_ + " | " + _)
}


class JointStrategyHistory[State](val neighbourIds: Iterable[Any], val domain: Seq[State]) {
  import scala.collection.mutable.HashMap
  /**
   * Helper function for creating all combinations of a given size from the elements of a list.
   * The order does matter, so (1,2) is not the same as (2,1).
   * For example:
   * cartesian(List(1,2), 2) will yield List(List(1,1), List(1,2), List(2,1), List(2,2))
   */
  def cartesian[A](xs: Traversable[Traversable[A]]): Seq[Seq[A]] = {
    xs.foldLeft(Seq(Seq.empty[A])) {
      (x, y) => for (a <- x.view; b <- y) yield a :+ b
    }
  }

  val nNeighbours = neighbourIds.size

  // All the possible state configurations as a list of lists.
  // This corresponds to Y_{-i}, where i is the id of this vertex.
  val neighbourhoods = cartesian(Traversable.fill(nNeighbours)(domain))

  private val history: HashMap[Seq[State], Double] =
    HashMap(neighbourhoods map { _ -> 1.0 / neighbourhoods.size } :_* )

  private var age = neighbourhoods.size

  def get(config: Seq[State]) = history.get(config)

  def updateWithMap(config: Map[Any, State]) {
    val sorted = config.toSeq sortBy {_._1.asInstanceOf[Int]} map {_._2}
    update(sorted)
  }

  def update(config: Seq[State]) {
    if (config.size != neighbourIds.size) {
      throw new Exception(s"Update key has to be exactly ${neighbourIds.size} long!")
    } else {
      history foreach { case (cfg, p) =>
        val indicator =  if (cfg == config) 1 else 0
        val pNew = (history(cfg) * age + indicator) / (age + 1)
        history.update(cfg, pNew)
      }
      age += 1
    }
  }
}

