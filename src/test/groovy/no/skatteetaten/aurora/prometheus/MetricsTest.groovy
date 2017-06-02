package no.skatteetaten.aurora.prometheus

import static no.skatteetaten.aurora.prometheus.collector.Operation.withMetrics
import static no.skatteetaten.aurora.prometheus.collector.Status.StatusValue.CRITICAL
import static no.skatteetaten.aurora.prometheus.collector.Status.StatusValue.OK
import static no.skatteetaten.aurora.prometheus.collector.Status.StatusValue.UNKNOWN
import static no.skatteetaten.aurora.prometheus.collector.Status.StatusValue.WARNING
import static no.skatteetaten.aurora.prometheus.collector.Status.status

import io.prometheus.client.CollectorRegistry
import no.skatteetaten.aurora.prometheus.collector.HttpMetricsCollector
import no.skatteetaten.aurora.prometheus.collector.Size
import spock.lang.Specification
import spock.lang.Unroll

class MetricsTest extends Specification {

  def registry = new CollectorRegistry(true)
  def config = MetricsConfig.init(registry, [] as Set)

  def "should have metrics registered"() {

    expect:
      def samples = config.metricFamilySamples().toSet()

      !samples.isEmpty()

      samples.collect { it.name }.containsAll(
          ["operations", "sizes", "statuses", "jvm_gc_hist", "logback_appender_total"])

  }

  def "record ok staus"() {
    when:
      status("myStatus", OK)
    then:
      statusFor("myStatus") == 0
  }

  def "record warning staus"() {
    when:
      status("myStatus", WARNING)
    then:
      statusFor("myStatus") == 1
  }

  def "record critical staus"() {
    when:
      status("myStatus", CRITICAL)
    then:
      statusFor("myStatus") == 2
  }

  def "record unknown staus"() {
    when:
      status("myStatus", UNKNOWN)
    then:
      statusFor("myStatus") == 3
  }

  @Unroll
  def "should record size #value metrics "() {

    expect:
      Size.size(name, type, value)

      String[] names = ["name", "type"]
      String[] values = [name, type]
      def result = config.getSampleValue("sizes", names, values)
      result == value

    where:
      name   | type         | value
      "test" | "feed"       | 1
      "test" | "feed"       | 2
      "test" | "feed"       | 3
      "bar"  | "threadpool" | 1

  }

  def "should record database write operations metric"() {

    when:
      withMetrics("test", "DATABASE_WRITE", { "simulating operation" })

    then:
      String[] names = ["result", "type", "name"]
      String[] values = ["success", "DATABASE_WRITE", "test"]
      def result = config.getSampleValue("operations_count", names, values)
      result == 1.0

  }

  def "should record operation metric"() {

    when:
      withMetrics("test", { "simulating operation" })

    then:
      String[] names = ["result", "type", "name"]
      String[] values = ["success", "operation", "test"]
      def result = config.getSampleValue("operations_count", names, values)
      result == 1.0

  }

  def "should record operation metric failure"() {

    when:

      try {
        withMetrics("test", { throw new RuntimeException(("foo")) })
      } catch (RuntimeException e) {

      }

    then:
      String[] names = ["result", "type", "name"]
      String[] values = ["RuntimeException", "operation", "test"]
      def result = config.getSampleValue("operations_count", names, values)
      result == 1.0

  }

  def "should record logging metrics"() {

    expect:
      String[] names = ["level"]
      String[] values = ["debug"]
      def result = config.getSampleValue("logback_appender_total", names, values)
      result == 1.0

  }

  def "should record http client Metrics "() {

    given:

      def httpClientCollector = new HttpMetricsCollector(true, new HttpMetricsCollectorConfig())
      def config = MetricsConfig.init(new CollectorRegistry(true), [httpClientCollector] as Set)


    when:
      def start = System.nanoTime()
      httpClientCollector.record("POST", "http://autobot.rules/r2d2", 200, start)

    then:
      String[] names = ["http_method", "http_status", "http_status_group", "path"]
      String[] values = ["POST", "200", "SUCCESSFUL", "autobot.rules_r2d2"]
      def result = config.getSampleValue("http_client_requests_count", names, values)
      result == 1.0

  }

  def "should record http server Metrics "() {

    given:

      def httpSeverCollector = new HttpMetricsCollector(false, new HttpMetricsCollectorConfig())
      def config = MetricsConfig.init(new CollectorRegistry(true), [httpSeverCollector] as Set)

    when:
      def start = System.nanoTime()
      httpSeverCollector.record("POST", "/api/r2d2", 200, start)


    then:

      String[] names = ["http_method", "http_status", "http_status_group", "path"]
      String[] values = ["POST", "200", "SUCCESSFUL", "api_r2d2"]

      def result = config.getSampleValue("http_server_requests_count", names, values)
      result == 1.0

  }

  def statusFor(name) {
    config.getSampleValue("statuses", ["name"] as String[], [name] as String[])
  }
}

