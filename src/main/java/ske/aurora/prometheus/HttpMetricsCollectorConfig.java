package ske.aurora.prometheus;

import java.util.LinkedHashMap;
import java.util.Map;

import ske.aurora.prometheus.utils.MetricsMode;

public class HttpMetricsCollectorConfig {

    private MetricsMode mode = MetricsMode.ALL;
    private LinkedHashMap<String, String> metricsPathLabelGroupings = new LinkedHashMap<>();
    private LinkedHashMap<String, String> includes = new LinkedHashMap<>();
    private LinkedHashMap<String, String> excludes = new LinkedHashMap<>();

    public HttpMetricsCollectorConfig(MetricsMode mode,
        LinkedHashMap<String, String> metricsPathLabelGroupings,
        LinkedHashMap<String, String> includes,
        LinkedHashMap<String, String> excludes) {
        this.mode = mode;
        this.metricsPathLabelGroupings = metricsPathLabelGroupings;
        this.includes = includes;
        this.excludes = excludes;
    }

    public Map<String, String> getMetricsPathLabelGroupings() {
        return metricsPathLabelGroupings;
    }

    public MetricsMode getMode() {
        return mode;
    }

    public void setMode(MetricsMode mode) {
        this.mode = mode;
    }

    public Map<String, String> getIncludes() {
        return includes;
    }

    public Map<String, String> getExcludes() {
        return excludes;
    }
}
