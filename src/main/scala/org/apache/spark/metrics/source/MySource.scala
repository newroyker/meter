package org.apache.spark.metrics.source

import com.codahale.metrics.{Counter, Histogram, MetricRegistry}
import org.apache.spark.SparkEnv
import org.apache.spark.sql.SparkSession

class MySource extends Source {
  override val sourceName: String = "MySource"

  override val metricRegistry: MetricRegistry = new MetricRegistry

  val FOO: Histogram = metricRegistry.histogram(MetricRegistry.name("fooHistory"))
  val FOO_COUNTER: Counter = metricRegistry.counter(MetricRegistry.name("fooCounter"))
}

object MySourceDemo extends App {
  /*val spark: SparkSession = SparkSession
    .builder
    .master("local[*]")
    .appName("MySourceDemo")
    .config("spark.driver.host", "localhost")
    .config("spark.metrics.conf.*.sink.console.class", "org.apache.spark.metrics.sink.ConsoleSink")
    .config("spark.metrics.conf.*.source.mysource.class", "org.apache.spark.metrics.source.MySource")
    .getOrCreate()

  val source: MySource = SparkEnv.get.metricsSystem.getSourcesByName("MySource").head.asInstanceOf[MySource]*/

  val spark: SparkSession = SparkSession
    .builder
    .master("local[*]")
    .appName("MySourceDemo")
    .config("spark.driver.host", "localhost")
    .config("spark.metrics.conf.*.sink.console.class", "org.apache.spark.metrics.sink.ConsoleSink")
    .getOrCreate()

  val source: MySource = new MySource

  SparkEnv.get.metricsSystem.registerSource(source)

  (1 to 100).foreach(_ => source.FOO.update(1L))

  (1 to 100).foreach(_ => source.FOO_COUNTER.inc(1L))

  println(s"### FOO_PARSED: ${source.FOO.getSnapshot.getValues.mkString(",")}")

  println(s"### FOO_COUNTER: ${source.FOO_COUNTER.getCount}")

  SparkEnv.get.metricsSystem.report

  spark.stop()
}
