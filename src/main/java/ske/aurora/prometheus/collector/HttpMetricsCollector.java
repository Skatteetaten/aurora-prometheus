package ske.aurora.prometheus.collector;

import static ske.aurora.prometheus.utils.PrometheusUrlNormalizer.normalize;

import java.util.List;

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

    public void record(String method, String requestUri, int statusCode, long start) {
        long duration = System.nanoTime() - start;

        if (!config.shouldRecord(requestUri)) {
            return;
        }

        String path = config.groupUrl(requestUri).orElse(normalize(requestUri, isClient));

        requests.labels(
            method,
            String.valueOf(statusCode),
            HttpStatusSeries.valueOf(statusCode).name(),
            path
        ).observe(duration / Collector.NANOSECONDS_PER_SECOND);
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return requests.collect();
    }

}
