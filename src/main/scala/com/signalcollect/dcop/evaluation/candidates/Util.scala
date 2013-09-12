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
 */

package com.signalcollect.dcop.evaluation.candidates
/*
 * Utility functions that may be shared across multiple algorithms
 */
object Util {
  /*
   * Get the maximal values that are equal to each other.
   */
  def maxValuesBy[T : Ordering](elems: Iterable[T]): Iterable[T] = {
    def maxValues(xss: List[T], currentMax: List[T]): List[T] = xss match {
      case Nil => currentMax
      case (x::xs) => currentMax match {
        case Nil => maxValues(xs, List(x)) // No current max, take the first element as the new max
        case maxs@(max::_) => {
          val cmp = implicitly[Ordering[T]].compare(x, max)
          if (cmp < 0) {
            maxValues(xs, maxs)
          } else if (cmp == 0) {
            maxValues(xs, x::maxs)
          } else {
            maxValues(xs, List(x))
          }
        }
      }
    }
    maxValues(elems.toList, List())
  }

  /* Generate the cartesian product of multiple sets (represented as traversables).
   * Similar to
   * A x B x C = { (a1, b1, c1), ..., (an, bn, cn) }
   * but with Seqs instead of Sets
   */
  def cartesian[A](xs: Traversable[Traversable[A]]): Seq[Seq[A]] = {
    xs.foldLeft(Seq(Seq.empty[A])){
      (x, y) => for (a <- x.view; b <- y) yield a :+ b
    }
  }
}
