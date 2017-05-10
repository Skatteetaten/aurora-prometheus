package ske.aurora.prometheus.collector;

import static ske.aurora.prometheus.utils.PrometheusUrlNormalizer.normalize;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.prometheus.client.Collector;
import io.prometheus.client.Histogram;
import ske.aurora.prometheus.HttpMetricsCollectorConfig;
import ske.aurora.prometheus.utils.HttpStatusSeries;

public class HttpMetricsCollector extends Collector {

    private final Histogram requests;
    private boolean isClient;
    private HttpMetricsCollectorConfig config;

    public HttpMetricsCollector(boolean isClient, HttpMetricsCollectorConfig config) {
        this.isClient = isClient;
        this.config = config;

        requests = Histogram.build()
            .name(String.format("http_%s_requests", isClient ? "client" : "server"))
            .help(String.format("Http %s requests", isClient ? "client" : "server"))
            .labelNames("http_method", "http_status", "http_status_group", "path")
            .create();
    }

    public boolean shouldRecord(String requestUri) {

        switch (config.getMode()) {
        case INCLUDE_MAPPINGS:
            if (!findMatchingPath(config.getMetricsPathLabelGroupings(), requestUri).isPresent()) {
                return false;
            }
            break;
        case INCLUDE:
            if (!findMatchingPath(config.getIncludes(), requestUri).isPresent()) {
                return false;
            }
            break;
        case EXCLUDE:
            if (findMatchingPath(config.getExcludes(), requestUri).isPresent()) {
                return false;
            }
            break;
        default:
            break;
        }
        return true;
    }

    public void record(String method, String requestUri, int statusCode, long start) {

        if (!shouldRecord(requestUri)) {
            return;
        }

        String path = findMatchingPath(config.getMetricsPathLabelGroupings(), requestUri)
            .orElse(normalize(requestUri, isClient));

        long duration = System.nanoTime() - start;
        requests.labels(
            method,
            String.valueOf(statusCode),
            HttpStatusSeries.valueOf(statusCode).name(),
            path
        ).observe(duration / Collector.NANOSECONDS_PER_SECOND);
    }

    private Optional<String> findMatchingPath(Map<String, String> mappings, String url) {

        return mappings.entrySet().stream()
            .filter(e -> url.matches(e.getValue()))
            .map(Map.Entry::getKey)
            .findFirst();

    }

    @Override
    public List<MetricFamilySamples> collect() {
        return requests.collect();
    }

}
