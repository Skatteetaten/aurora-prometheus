package ske.aurora.prometheus

import static ske.aurora.prometheus.collector.Operation.OperationType.DATABASE_WRITE
import static ske.aurora.prometheus.collector.Operation.withMetrics
import static ske.aurora.prometheus.collector.Status.status

import ske.aurora.prometheus.collector.HttpMetricsCollector
import ske.aurora.prometheus.collector.Status
import spock.lang.Specification

class MetricsTest extends Specification {

  def config = MetricsConfig.init([] as Set)

  def "should have metrics registered"() {

    expect:
      def samples = config.metricFamilySamples().toSet()

      samples.size() == 23

  }

  def "should record status metrics "() {

    when:
      status("test", Status.StatusValue.OK)

    then:
      String[] names = ["name"]
      String[] values = ["test"]
      def result = config.getSampleValue("statuses", names, values)
      result == 0

  }

  def "should record database write operations metric"() {

    when:
      withMetrics("test", DATABASE_WRITE, { "foo" })

    then:
      String[] names = ["result", "type", "name"]
      String[] values = ["success", "DATABASE_WRITE", "test"]
      def result = config.getSampleValue("operations_count", names, values)
      result == 1.0

  }
  def "should record operation metric"() {

    when:
      withMetrics("test", { "foo" })

    then:
      String[] names = ["result", "type", "name"]
      String[] values = ["success", "OTHER", "test"]
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
      String[] values = ["RuntimeException", "OTHER", "test"]
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
      def config = MetricsConfig.init([httpClientCollector] as Set)


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
      def config = MetricsConfig.init([httpSeverCollector] as Set)

    when:
      def start = System.nanoTime()
      httpSeverCollector.record("POST", "/api/r2d2", 200, start)


    then:

      String[] names = ["http_method", "http_status", "http_status_group", "path"]
      String[] values = ["POST", "200", "SUCCESSFUL", "api_r2d2"]

      def result = config.getSampleValue("http_server_requests_count", names, values)
      result == 1.0

  }

}
