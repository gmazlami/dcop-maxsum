package com.signalcollect.dcop.benchmark

object BenchmarkModes extends Enumeration{
  type BenchmarkModes = Value
  val SyncConflictsOverTime , SyncResultingConflicts , AsyncResultingConflicts , SyncStepsToConvergence , SyncTimeToConvergence , AsyncTimeToConvergence = Value  
}