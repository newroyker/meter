package org.apache.spark.metrics.source

import com.codahale.metrics.{Counter, Histogram, MetricRegistry}
import org.apache.spark.SparkEnv
import org.apache.spark.sql.SparkSession
import org.apache.spark.util.Utils

class MySource extends Source {
  override def sourceName: String = "MySource"

  override def metricRegistry: MetricRegistry = new MetricRegistry

  val FOO: Histogram = metricRegistry.histogram(MetricRegistry.name("fooHistory"))
  val FOO_COUNTER: Counter = metricRegistry.counter(MetricRegistry.name("fooCounter"))
  metricRegistry.register("FooParsed", FOO)
  metricRegistry.register("FooCounter", FOO_COUNTER)
}

object MySourceDemo extends App {
  implicit val spark: SparkSession = SparkSession
    .builder
    .master("local[*]")
    .appName("MySourceDemo")
    .config("spark.driver.host", "localhost")
    .config("spark.metrics.conf.*.sink.console.class", "org.apache.spark.metrics.sink.ConsoleSink")
    .config("spark.metrics.conf.*.source.mysource.class", "org.apache.spark.metrics.source.MySource")
    .getOrCreate()

  println(s"### Class can be found: ${Utils.classForName("org.apache.spark.metrics.source.MySource")}")

  spark.sparkContext.setLogLevel("ERROR")

  SparkEnv.get.metricsSystem.getSourcesByName("MySource")

  val source: MySource = SparkEnv.get.metricsSystem.getSourcesByName("MySource").head.asInstanceOf[MySource]

  (1 to 100).foreach(_ => source.FOO.update(1L))

  (1 to 100).foreach(_ => source.FOO_COUNTER.inc(1L))

  println(s"### FOO_PARSED: ${source.FOO.getSnapshot.getValues.mkString(",")}")

  println(s"### FOO_COUNTER: ${source.FOO_COUNTER.getCount}")

  SparkEnv.get.metricsSystem.report

  spark.stop()
}
