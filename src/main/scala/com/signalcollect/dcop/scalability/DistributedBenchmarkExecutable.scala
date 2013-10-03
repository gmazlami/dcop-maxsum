package com.signalcollect.dcop.scalability

import com.signalcollect.GraphBuilder
import com.signalcollect.Graph
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import java.io.File
import com.signalcollect.nodeprovisioning.torque.TorqueHost
import com.signalcollect.nodeprovisioning.torque.TorqueJobSubmitter
import com.signalcollect.nodeprovisioning.torque.TorquePriority
import com.signalcollect.nodeprovisioning.torque.LocalHost
import com.signalcollect.nodeprovisioning.torque.ExecutionHost
import com.signalcollect.dcop.graphs.GraphProvider
import com.signalcollect.nodeprovisioning.NodeProvisioner
import com.signalcollect.nodeprovisioning.torque.TorqueNodeProvisioner
import com.signalcollect.interfaces.AggregationOperation
import com.signalcollect.dcop.vertices.VariableVertex
import com.signalcollect.configuration.TerminationReason
import com.signalcollect.dcop.graphs.FactorGraphProvider
import com.signalcollect.dcop.graphs.DSAGraphProvider
import com.signalcollect.dcop.graphs.BRGraphProvider
import scala.collection.mutable.HashMap
import com.signalcollect.dcop.vertices.id.MaxSumId
import com.signalcollect.dcop.vertices.id.MaxSumId
import scala.collection.mutable.ArrayBuffer

class DistributedBenchmarkExecutable(val algorithmName: String,
  val executionConfig: ExecutionConfiguration,
  val benchmarkConfig: BenchmarkConfiguration,
  val graphProvider: GraphProvider,
  val algorithmType: AlgorithmType.Value,
  val intervalList: List[Long],
  val graphSize: Int = 0,
  val pSchedule: Double = 0.0) {

  def jvmParameters = " -Xmx31000m" +
    " -Xms31000m" +
    " -XX:+AggressiveOpts" +
    " -XX:+AlwaysPreTouch" +
    " -XX:+UseNUMA" +
    " -XX:-UseBiasedLocking" +
    " -XX:MaxInlineSize=1024"

  def assemblyPath = "./target/scala-2.10/signal-collect-2.1-SNAPSHOT.jar" //TODO: assemble and change to path for this project
  val assemblyFile = new File(assemblyPath)
  val jdkPath = ""
  val userName = "mazlami"
  val password = "YL3vVWcU"
  val hostName = "kraken.ifi.uzh.ch"
  val numOfMachines = 1
  val submitter = new TorqueJobSubmitter(username = userName, hostname = hostName)
  val prio = TorquePriority.fast
  val kraken = new TorqueHost(jobSubmitter = submitter,
    						  localJarPath = assemblyPath,
						      jvmParameters = jvmParameters,
						      jdkBinPath = jdkPath,
						      priority = prio)
  val nodeProvisioner = new TorqueNodeProvisioner(kraken, numOfMachines)
  var graph: Graph[Any, Any] = null

  algorithmType match {
    case AlgorithmType.MS => graph = graphProvider.asInstanceOf[FactorGraphProvider].construct(nodeProvisioner)
    case AlgorithmType.DSAA => graph = graphProvider.asInstanceOf[DSAGraphProvider].construct(nodeProvisioner)
    case AlgorithmType.DSAB => graph = graphProvider.asInstanceOf[DSAGraphProvider].construct(nodeProvisioner)
    case AlgorithmType.BR => graph = graphProvider.asInstanceOf[BRGraphProvider].construct(nodeProvisioner)
  }

  def run() = {
    algorithmType match {
      case AlgorithmType.DSAA => executeBenchmarkNonMS()
      case AlgorithmType.DSAB => executeBenchmarkNonMS()
      case AlgorithmType.BR => executeBenchmarkNonMS()
      case AlgorithmType.MS => executeBenchmarkMS()
    }
  }

  private def executeBenchmarkNonMS(): Any = {
    benchmarkConfig.mode match {
      case BenchmarkModes.SyncConflictsOverSteps => conflictsOverSteps
      case BenchmarkModes.SyncStepsToConvergence => stepsToConvergence
      case _ => println("Unknown BenchmarkMode. Exiting.."); System.exit(-1)
    }
  }

  private def executeBenchmarkMS(): Any = {
    benchmarkConfig.mode match {
      case BenchmarkModes.SyncConflictsOverSteps => conflictsOverSteps
      case BenchmarkModes.AsyncConflictsOverTime => conflictsOverTime
      case BenchmarkModes.SyncConflictsOverTime => conflictsOverTime
      case BenchmarkModes.SyncStepsToConvergence => stepsToConvergence
      case BenchmarkModes.AsyncTimeToConvergence => timeToConvergence
      case BenchmarkModes.SyncTimeToConvergence => timeToConvergence
      case _ => println("Unknown BenchmarkMode. Exiting.."); System.exit(-1)
    }
  }

  /*
   * Benchmark execution methods:
   */
  private def conflictsOverSteps(): List[Tuple2[Int, Int]] = {
    var conflictsOverStepsList: List[Tuple2[Int, Int]] = List()
    for (step <- 0 to benchmarkConfig.stepsLimit - 1) {
      graph.execute(executionConfig)
      if (algorithmType == AlgorithmType.MS) {
        graph.foreachVertexWithGraphEditor(ge => { vertex =>
          {
            vertex match {
              case v: VariableVertex => v.tellNeighborsAboutColor(ge)
              case f: Any =>
            }
          }
        })
      }
      conflictsOverStepsList :+= (step, graph.aggregate(benchmarkConfig.aggregationOperation).asInstanceOf[Int])
    }
    conflictsOverStepsList
  }

  private def stepsToConvergence(): Long = {
    val executionInfo = graph.execute(executionConfig)
    if (executionInfo.executionStatistics.terminationReason == TerminationReason.Converged) {
      executionInfo.executionStatistics.signalSteps
    } else {
      -1
    }
  }

  private def conflictsOverTime(): List[Tuple2[Long, Int]] = {
    var conflictsOverTimeList: List[Tuple2[Long, Int]] = List()
    for (interval <- intervalList) {
      val longInterval = interval.asInstanceOf[Long]
      graph.execute(copyExecutionConfigWithTimeLimit(interval, executionConfig))
      if (algorithmType == AlgorithmType.MS) {
        graph.foreachVertexWithGraphEditor(ge => { vertex =>
          {
            vertex match {
              case v: VariableVertex => v.tellNeighborsAboutColor(ge)
              case f: Any =>
            }
          }
        })
      }
      conflictsOverTimeList :+= (longInterval, graph.aggregate(benchmarkConfig.aggregationOperation).asInstanceOf[Int])
    }
    conflictsOverTimeList
  }

  private def timeToConvergence(): Long = {
    val executionInfo = graph.execute(executionConfig)
    if (executionInfo.executionStatistics.terminationReason == TerminationReason.Converged) {
      executionInfo.executionStatistics.computationTime.toMillis
    } else {
      -1
    }
  }
  //--------------------------------------------------------

  /*
   * Auxiliary methods:
   */
  
  private def copyExecutionConfigWithTimeLimit(t: Long, config: ExecutionConfiguration) = {
    new ExecutionConfiguration().withExecutionMode(config.executionMode).withSignalThreshold(config.signalThreshold).withCollectThreshold(config.collectThreshold).withTimeLimit(t)
  }
  
  private def initializeFunctionConstantsOnMaxSum() = {
    
  }
}