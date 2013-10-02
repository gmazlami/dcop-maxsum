package com.signalcollect.dcop.graphs

import com.signalcollect.nodeprovisioning.torque.TorqueNodeProvisioner
import com.signalcollect.GraphBuilder

trait GraphProvider {

   def construct(nodeProvisioner : TorqueNodeProvisioner) = {
    GraphBuilder.withNodeProvisioner(nodeProvisioner).build
  }
}