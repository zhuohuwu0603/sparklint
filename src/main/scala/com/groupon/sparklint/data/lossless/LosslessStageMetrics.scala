/*
 Copyright 2016 Groupon, Inc.
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/
package com.groupon.sparklint.data.lossless

import com.groupon.sparklint.data.{SparklintStageMetrics, SparklintTaskMetrics}
import org.apache.spark.executor.TaskMetrics
import org.apache.spark.scheduler.TaskLocality.TaskLocality

/**
  * @author rxue
  * @since 9/22/16.
  */
case class LosslessStageMetrics(losslessMetricsRepo: Map[(TaskLocality, Symbol), LosslessTaskMetrics])
  extends SparklintStageMetrics {
  override def metricsRepo: Map[(TaskLocality, Symbol), SparklintTaskMetrics] = losslessMetricsRepo

  def merge(taskId: Long, taskType: Symbol, locality: TaskLocality, metrics: TaskMetrics): LosslessStageMetrics = {
    val newMetrics = losslessMetricsRepo.getOrElse(locality -> taskType, new LosslessTaskMetrics).merge(taskId, metrics)
    copy(losslessMetricsRepo + ((locality -> taskType) -> newMetrics))
  }
}

object LosslessStageMetrics {
  def empty: LosslessStageMetrics = new LosslessStageMetrics(Map.empty)
}
