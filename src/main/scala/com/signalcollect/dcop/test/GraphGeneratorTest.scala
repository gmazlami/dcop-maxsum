package com.signalcollect.dcop.test

import com.signalcollect.dcop.graphs.GraphGenerator

object GraphGeneratorTest extends App {

  val g = new GraphGenerator(300,3,3,"graphs/graph300.txt")
  g.generateToFile
  println("Finished!")
}