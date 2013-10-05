package com.signalcollect.dcop.scalability.conflicts

import com.signalcollect.dcop.benchmark.BenchmarkModes
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.dcop.benchmark.BenchmarkConfiguration
import com.signalcollect.dcop.evaluation.maxsum.MaxSumAlgorithm
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.dcop.evaluation.maxsum.MaxSumConflictAggregationOperation
import com.signalcollect.dcop.evaluation.dsa.DSAConflictAggregationOperation
import com.signalcollect.dcop.evaluation.dsa.DSAAlgorithm
import com.signalcollect.dcop.evaluation.candidates.DSAVariant
import com.signalcollect.dcop.evaluation.bestresponse.BRAlgorithm
import com.signalcollect.dcop.evaluation.bestresponse.BRConflictAggregationOperation
import com.signalcollect.dcop.io.ResultWriter
import com.signalcollect.dcop.graphs.FactorGraphProvider
import com.signalcollect.dcop.io.FileGraphReader
import com.signalcollect.dcop.scalability.DistributedBenchmarkExecutable
import com.signalcollect.dcop.scalability.AlgorithmType
import com.signalcollect.dcop.graphs.DSAGraphProvider
import com.signalcollect.dcop.graphs.BRGraphProvider
import com.signalcollect.dcop.io.DropboxResultHandler

object SyncConflictsOverStepsScaling extends App {

  /*
   * general properties
   */
  val fileName = "graphs/scaling/synthetic/graph300.txt"
  val graphName = "graph300"
  val isAdopt = false
  val graphSize = 300
  val steps = 2
  val benchmarkMode = BenchmarkModes.SyncConflictsOverSteps
  //------------------------------------------------

  /*
   * Properties for Graph loading
   */
//  val factorGraphProvider = new FactorGraphProvider(new FileGraphReader, fileName)
  val dsaaGraphProvider = new DSAGraphProvider(graphSize,0.45, DSAVariant.A,fileName, isAdopt)
//  val dsabGraphProvider = new DSAGraphProvider(graphSize,0.45, DSAVariant.B,fileName, isAdopt)
//  val brGraphProvider = new BRGraphProvider(graphSize,0.6,fileName, isAdopt)
  //------------------------------------------------

  
//  /*
//   * properties for MaxSum
//   */
//  val maxSumName = "MaxSum"
//  val MSexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withStepsLimit(1)
//  val MSbenchmarkConfig = new BenchmarkConfiguration(MSexecutionConfig, fileName, isAdopt, steps, new MaxSumConflictAggregationOperation, benchmarkMode)
//  val maxSumExecutable =  new DistributedBenchmarkExecutable("SyncMaxSum",
//		  											  MSexecutionConfig,
//		  											  MSbenchmarkConfig,
//		  											  factorGraphProvider,
//		  											  AlgorithmType.MS,
//		  											  null)

    /*
     * properties for DSA-A and DSA-B
     */
    val dsaAname = "DSAA"
    val dsaBname = "DSAB"
    val DSAexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withStepsLimit(1)
    val DSAbenchmarkConfig = new BenchmarkConfiguration(DSAexecutionConfig,fileName,isAdopt,steps,new DSAConflictAggregationOperation,benchmarkMode)
  	val dsaAexecutable =  new DistributedBenchmarkExecutable("DSA-A",
		  											  DSAexecutionConfig,
		  											  DSAbenchmarkConfig,
		  											  dsaaGraphProvider,
		  											  AlgorithmType.DSAA,
		  											  null,
		  											  graphSize,
		  											  0.45)
//    val dsaBexecutable =  new DistributedBenchmarkExecutable("DSA-B",
//		  											  DSAexecutionConfig,
//		  											  DSAbenchmarkConfig,
//		  											  dsabGraphProvider,
//		  											  AlgorithmType.DSAB,
//		  											  null,
//		  											  graphSize,
//		  											  0.45)
//  
//    
//    /*
//     * properties for Best-Response
//     */
//    val brName = "BestResponse"
//    val BRexecutionConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous).withCollectThreshold(0).withSignalThreshold(0).withStepsLimit(1)
//    val BRbenchmarkConfig = new BenchmarkConfiguration(BRexecutionConfig,fileName,isAdopt,steps,new BRConflictAggregationOperation,benchmarkMode)
//  	val brExecutable = new DistributedBenchmarkExecutable("SyncMaxSum",
//		  											  BRexecutionConfig,
//		  											  BRbenchmarkConfig,
//		  											  brGraphProvider,
//		  											  AlgorithmType.BR,
//		  											  null,
//		  											  graphSize,
//		  											  0.6)

  /*
   * result Containers
   */
  var maxSumConflicts: List[Tuple2[Long, Int]] = null
  var dsaAconflicts: List[Tuple2[Long, Int]] = null
  var dsaBconflicts: List[Tuple2[Long, Int]] = null
  var bestResponseConflicts: List[Tuple2[Long, Int]] = null

//  /*
//   * run evaluation for MaxSum:
//   */
//  println("Evaluating Max-Sum...")
//  maxSumConflicts = maxSumExecutable.run.asInstanceOf[List[Tuple2[Long, Int]]]
//  println("Max-Sum evaluted.")
//  printConflictList(maxSumConflicts)
//  handleResult(maxSumConflicts, benchmarkMode)
//  println("-----------------------")

  /*
     * run evaluation for DSA-A
     */
  println("Evaluating DSA-A...")
  dsaAconflicts = dsaAexecutable.run.asInstanceOf[List[Tuple2[Long, Int]]]
  println("DSA-A evaluated.")
  printConflictList(dsaAconflicts)
  handleResult(dsaAconflicts, benchmarkMode)
  println("-----------------------")

//  /*
//     * run evaluation for DSA-B
//     */
//  println("Evaluating DSA-B...")
//  dsaBconflicts = dsaBexecutable.run.asInstanceOf[List[Tuple2[Long, Int]]]
//  println("DSA-B evaluated.")
//  printConflictList(dsaBconflicts)
//  handleResult(dsaBconflicts, benchmarkMode)
//  println("-----------------------")
//
//  /*
//     * run evaluation for Best-Response
//     */
//  println("Evaluating Best-Response...")
//  bestResponseConflicts = brExecutable.run.asInstanceOf[List[Tuple2[Long, Int]]]
//  println("Best-Response evaluated.")
//  printConflictList(bestResponseConflicts)
//  handleResult(bestResponseConflicts, benchmarkMode)
//  println("-----------------------")

  System.exit(0)

  def storeResultsToFile(results: Any, algorithm: String) = {
    val resultWriter = new ResultWriter(benchmarkMode, graphName, algorithm, results)
    resultWriter.write()
  }

  def printConflictList(list: List[Tuple2[Long, Int]]) = {
    list.foreach { el =>
      println(el._1 + " - " + el._2)
    }
  }
 
  def handleResult(results : Any, mode : BenchmarkModes.Value) = {
    val dbx = new DropboxResultHandler("graph300SyncConflictsSteps", "scaling/steps/conflicts",mode)
    dbx.handleResult(results)
  }
}