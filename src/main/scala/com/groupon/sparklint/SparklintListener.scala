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
package com.groupon.sparklint

import com.groupon.sparklint.common.{SparklintConfig, Scheduler}
import com.groupon.sparklint.events.{BufferedEventSource, CompressedEventState, EventSourceManager}
import org.apache.spark.scheduler.SparkListenerEvent
import org.apache.spark.{SparkConf, SparkFirehoseListener}

/**
  * The listener that will be created when provided in --conf spark.extraListeners
  *
  * @author rxue
  * @since 8/18/16.
  */
class SparklintListener(appId: String, appName: String) extends SparkFirehoseListener {
  def this(conf: SparkConf) = {
    this(conf.get("spark.app.id", "AppId"), conf.get("spark.app.name", "AppName"))
  }

  private val eventSource   = BufferedEventSource(appId, new CompressedEventState())
  private val sourceManager = new EventSourceManager(eventSource)

  val scheduler = new Scheduler()
  val config    = SparklintConfig()
  val server    = new SparklintServer(sourceManager, scheduler, config)
  server.run()

  eventSource.runnit()

  override def onEvent(event: SparkListenerEvent): Unit = {
    // push unconsumed events to the queue so the listener can keep consuming on the main thread
    eventSource.push(event)
  }
}
