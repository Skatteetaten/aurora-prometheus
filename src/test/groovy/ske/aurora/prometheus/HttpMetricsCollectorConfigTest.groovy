package ske.aurora.prometheus

import static ske.aurora.prometheus.HttpMetricsCollectorConfig.MetricsMode.ALL
import static ske.aurora.prometheus.HttpMetricsCollectorConfig.MetricsMode.EXCLUDE
import static ske.aurora.prometheus.HttpMetricsCollectorConfig.MetricsMode.INCLUDE
import static ske.aurora.prometheus.HttpMetricsCollectorConfig.MetricsMode.INCLUDE_MAPPINGS

import spock.lang.Specification
import spock.lang.Unroll

class HttpMetricsCollectorConfigTest extends Specification {

  @Unroll
  def "url #url in #mode should be #match"() {

    expect:
      def config = new HttpMetricsCollectorConfig(mode,
          ["foo": ".*foo.com.*"], ["bar": ".*bar.com.*"], ["baz": ".*baz.com.*"])
      config.shouldRecord(url) == match


    where:
      url                  | mode             | match
      "http://foo.com/api" | INCLUDE_MAPPINGS | true
      "http://bar.com/api" | INCLUDE_MAPPINGS | false
      "http://foo.com/api" | INCLUDE          | false
      "http://bar.com/api" | INCLUDE          | true
      "http://foo.com/api" | EXCLUDE          | true
      "http://bar.com/api" | EXCLUDE          | true
      "http://baz.com/api" | EXCLUDE          | false
      "http://foo.com/api" | ALL              | true
      "http://bar.com/api" | ALL              | true
      "http://baz.com/api" | ALL              | true

  }

  def "should map url"() {
    given:

      def config = new HttpMetricsCollectorConfig(ALL, ["foo": ".*foo.com.*"], [:], [:])

    when:
      def result = config.groupUrl("http://foo.com")

    then:
      result.isPresent()
      result.get() == "foo"
  }

  def "should not map url"() {
    given:

      def config = new HttpMetricsCollectorConfig(ALL, ["foo": ".*foo.com.*"], [:], [:])

    when:
      def result = config.groupUrl("http://bar.com")

    then:
      !result.isPresent()
  }
}
