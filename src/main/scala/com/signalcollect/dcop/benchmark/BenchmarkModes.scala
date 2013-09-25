package com.signalcollect.dcop.benchmark

object BenchmarkModes extends Enumeration{
  type BenchmarkModes = Value
  val  SyncConflictsOverSteps , SyncConflictsOverTime , AsyncConflictsOverTime , SyncStepsToConvergence , SyncTimeToConvergence , AsyncTimeToConvergence = Value  
}