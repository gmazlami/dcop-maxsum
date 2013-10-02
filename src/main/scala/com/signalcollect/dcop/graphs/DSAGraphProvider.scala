package com.signalcollect.dcop.graphs

import com.signalcollect.Graph
import com.signalcollect.dcop.evaluation.candidates.DSAVertexBuilder
import com.signalcollect.dcop.evaluation.candidates.BinaryConstraintGraphProvider
import com.signalcollect.GraphBuilder
import com.signalcollect.nodeprovisioning.torque.TorqueNodeProvisioner
import com.signalcollect.dcop.evaluation.candidates.DSAVariant
import com.signalcollect.StateForwarderEdge

class DSAGraphProvider(val graphSize: Int,
  val pSchedule: Double,
  val dsaVariant: DSAVariant.Value,
  val numColors: Int,
  val file: String,
  val isInputAdopt: Boolean) extends GraphProvider {

  override def construct(nodeProvisioner: TorqueNodeProvisioner) = {
    var graph: Graph[Any, Any] = null
    val algorithm = new DSAVertexBuilder(false, dsaVariant, pSchedule)
    val graphProvider: BinaryConstraintGraphProvider = new BinaryConstraintGraphProvider(graphSize, 2, numColors, loadFrom = file, isAdopt = isInputAdopt)
    val graphBuilder = new GraphBuilder[Any, Any]()
    val edgeBuilder = (x: Int, y: Int) => new StateForwarderEdge(y) 

    graph = graphBuilder.withNodeProvisioner(nodeProvisioner).build
    graphProvider.populate(graph, algorithm, edgeBuilder)
    graph.awaitIdle

    graph
  }
}