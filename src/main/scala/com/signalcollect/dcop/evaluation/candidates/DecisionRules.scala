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
import scala.Some
import math.{max}



/**
 * The rule with which to pick a new state from a list of (State, Utility)-tuples.
 * NOTE: The current state is special to many decision rules. This is why it is a seperate argument.
 * @tparam State A vertex' state
 */
trait DecisionRule[State] {
  def decisionRule(currentState: (State, Payoff)): Seq[(State, Payoff)] => State
}


/**
 * If there is no state that has a STRICTLY greater utility associated with it,
 * the current state is chosen again.
 * @tparam State A vertex' state
 */
trait ArgmaxADecision[State] extends DecisionRule[State] {
  def decisionRule(currentState: (State, Payoff)) = {
    case Nil => currentState._1
    case xs => {
      val sorted = xs sortWith { _._2 > _._2 }
      val maxPayoff = sorted.head._2
     
      if (maxPayoff > currentState._2) {
        val rand = new Random
        val bestStates = sorted takeWhile { _._2 == maxPayoff }
        bestStates(rand.nextInt(bestStates.size))._1
      } else {
        currentState._1
      }
    }
  }
}


/**
 * If there are states that have the same utility as the one of the current state,
 * the new state gets randomly chosen from this set of best states.
 * @tparam State A vertex' state
 */
trait ArgmaxBDecision[State] extends DecisionRule[State] {
  var stepCounter: Int
  override def decisionRule(currentState: (State, Payoff)) = {
    case Nil => currentState._1
    case otherStates => {
      val allStates = otherStates :+ currentState
      val sortedStates = allStates.sortWith(_._2 > _._2)
      val referenceState = sortedStates.head
      val candidateStates = sortedStates takeWhile { st =>
        st._2 == referenceState._2
      }
      val rand = new scala.util.Random
      candidateStates(rand.nextInt(candidateStates.size))._1
    }
  }
}

/**
 * Just like argmaxB: If there are states that have the same utility as the one of the current state,
 * the new state gets randomly chosen from this set of best states.
 * However, there is a chance (inertia) that the current state is chosen regardless of how much
 * better another state would be.
 * @tparam State A vertex' state
 */
trait ArgmaxBIDecision[State] extends ArgmaxBDecision[State] {
  val inertia: Probability
  val rand = scala.util.Random

  override def decisionRule(currentState: (State, Payoff)) = { otherStates =>
    val argmaxBDecision = super.decisionRule(currentState)(otherStates)
    if (rand.nextDouble() <= inertia) currentState._1
    else argmaxBDecision
  }
}

trait SimulatedAnnealingDecision[State] extends DecisionRule[State] {
  // Time counter used for calculating temperature
  var time: Int = 0 //(System.nanoTime() - startTime)/

  // exponent
  val k: Int
  val const: Int

  def temperature(i: Int): Double = {
    const.toDouble / math.pow(i, k)
  }

  def decisionRule(currentState: (State, Payoff)): (Seq[(State, Payoff)]) => State = { xs =>
    if (xs.size != 1) {
      throw new Exception("[error] Simulated Annealing needs exactly one state to evaluate")
    }
    val rand = new scala.util.Random
    val currentUtility = currentState._2
    val otherUtility = xs(0)._2
    val newState = xs(0)._1
    val delta = otherUtility - currentUtility
    val st = if (delta < 0) {
        // The new state does not improve utility but maybe it will be accepted as this vertex' state anyway
        val adopt = rand.nextDouble
        if (adopt < math.exp(delta / temperature(time))) {
          // We choose the new state (to explore) over the old state with probability (e(delta/t_i))
          newState
        } else {
          // With probability 1 - (e(delta/t_i)) we keep the old state which is better
          currentState._1
        }
      } else {
      // The new state improves utility (delta>0), so we adopt the new state
      newState
    }
    time += 1
    st
  }
}

trait LogisticDecision[State] extends DecisionRule[State] {
  import scala.math.{pow, E}

  var eta: Temperature
  val etaDecrement: Temperature

  /**
   * Create a probability distribution according to the Boltzmann function
   */
  def boltzmannDist[State](evaluatedStates: Seq[(State, Payoff)], eta: Double): Seq[(State, Probability)] = {
    val temp = max(eta, 0.01) // otherwise NaN
    val enumerators: Seq[Double] = evaluatedStates map {
      case (state, score) => pow(E, 1.0/temp * score)
    }
    val denominator = enumerators.sum
    val probabilities = enumerators map { (enum: Double) => enum / denominator }
    val possibleStates = evaluatedStates map (_._1)
    val evalStates = possibleStates zip probabilities
    evalStates
  }

  /**
   * Choose a random value from a probability distribution
   */
  def drawFrom[State](distribution: Seq[(State, Probability)]): State = {
    import scala.util.Random
    val rand = new Random()
    val sorted = distribution sortWith (_._2 < _._2)
    val sortedProbs = sorted map (_._2)
    val cumulativeProbs = sorted.zipWithIndex map {
      case ((s, p), i) => (s, p + sortedProbs.slice(0, i).sum)
    }
    val rnd = rand.nextDouble()
    (cumulativeProbs dropWhile { rnd > _._2 }).head._1
  }

  override def decisionRule(currentState: (State, Payoff)): Seq[(State, Payoff)] => State = { sts =>
    val newState = sts match {
      case Nil => currentState._1
      case xs => {
        val allEvaluatedStates = (currentState +: xs).toSet.toSeq
        val distribution = boltzmannDist(allEvaluatedStates, eta)
        drawFrom(distribution)
      }
    }

    eta -= etaDecrement // TODO: this should probably be done globally
    newState
  }
}



trait LinearProbability[State] extends DecisionRule[State] {

  val rand = Random
  val inertia = 1.0

  def cumulativeSum[T](xs: Iterable[T])(implicit num: Numeric[T]) = {
    xs.scanLeft(num.zero)(num.plus(_,_)).tail
  }

  def decisionRule(currentState: (State, Payoff)): (Seq[(State, Payoff)]) => State = { sts =>

    val evaluatedStates = sts ++ Seq(currentState)

    val payoffs = evaluatedStates map { _._2 }

    val sum = payoffs.sum

    val cdf = cumulativeSum(payoffs) map { _ / sum } toSeq

    var candidateState: Option[(State, Payoff)] = None

    for (k <- 0 until cdf.size) {
      val p = rand.nextDouble
      if (p <= cdf(k)) {
        candidateState = Some(evaluatedStates(k))
      }
    }

    candidateState.get._1

    if (rand.nextDouble < inertia) currentState._1
    else candidateState.get._1
  }
}



/**
 * Tabu Search algorithm
 * Pseudo code from Kari J. Nurmela's "Constructing Combinatorial Designs By Local Search"
 * 1. Select an initial solution s.
 * 2. While the stop condition is not met, do the following:
 *    (a) Let d be the move that leads to the lowest-cost neighbor
 *        of s such that
 *                (d not_elem_of T) OR (d elem_of T AND c(d(s)) < A(d,s))
 *    (b) Let s := d(s). Remove the oldest element in T and add d^(-1) to T.
 *
 * The default stopCriterion is met if all constraints are satisfied. In this case,
 * the vertex keeps it's last state.
 */
trait TabuListDecision[State] extends DecisionRule[State] {
  val stepsToRemember: Int
  var stepCounter: Int
  val random = new Random
  val id: Int

  case class Move(val move: (State, State), val payoff: Double) {
    def reversed = Move((move._2, move._1), payoff)
    override def equals(other: Any): Boolean = other match {
      case otherMove: Move => move.equals(otherMove)
      case otherwise => false
    }
    override def hashCode(): Int = move.hashCode()
  }

  /*
   * TabuList T of tuples representing a previously made move from state1 to state2
   * Note: The direction of the made move is added in reversed order.
   */
  val tabuList = new scala.collection.mutable.LinkedHashSet[Move]()

  /*
   * Permit moves to a solution that are tabu if their measure of 'goodness' is higher than the aspiration level
   * In this implementation the 'measure of goodness' is the utility of a local configuration,
   * which corresponds to the number of satisfied constraints. Higher is better.
   * Default: Don't permit such moves
   */
  def overruleTabuList(move: Move): Boolean = {
    if ((new Random().nextDouble() <= 0.1)) true
    else false
  }

  override def decisionRule(currentEvalState: (State, Payoff)): Seq[(State, Payoff)] => State = { evalStates =>
    val (currentState, currentPayoff) = currentEvalState

    var bestAllowedMoves = List[Move](Move((currentState, currentState), currentPayoff))

    for ((newState, payoff) <- evalStates) {
      val move = Move((currentState, newState), payoff)
      val isBetterOrEqualCurrent = payoff >= currentPayoff
      val isHigherThanCandidate =
        bestAllowedMoves.map( payoff >= _.payoff ).headOption getOrElse true
      val tabuListIsOverruled = overruleTabuList(move)
      val notInTabuList = !tabuList.contains(move)

      if (isBetterOrEqualCurrent && isHigherThanCandidate && (notInTabuList || tabuListIsOverruled)) {
        bestAllowedMoves = bestAllowedMoves match {
          case (m :: ms) if payoff > m.payoff => List(move)
          case otherwise => move :: bestAllowedMoves
        }
      }
    }

    val optBestMove = bestAllowedMoves match {
      case Nil => None
      case other => Some(bestAllowedMoves(random.nextInt(bestAllowedMoves.size)))
    }

    optBestMove.foreach { m =>
      tabuList.add(m)
      tabuList.add(m.reversed)
    }

    while (tabuList.size > stepsToRemember) { // should happen only once
      tabuList.remove(tabuList.head) // remove first element
    }

    optBestMove map { _.move._2 } getOrElse currentState
  }
}