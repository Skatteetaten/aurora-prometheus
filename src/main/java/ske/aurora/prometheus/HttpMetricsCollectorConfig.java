package ske.aurora.prometheus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class HttpMetricsCollectorConfig {

    private MetricsMode mode = MetricsMode.ALL;
    private LinkedHashMap<String, String> metricsPathLabelGroupings = new LinkedHashMap<>();
    private LinkedHashMap<String, String> includes = new LinkedHashMap<>();
    private LinkedHashMap<String, String> excludes = new LinkedHashMap<>();

    public HttpMetricsCollectorConfig() {
        //For spring
    }

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

    public boolean shouldRecord(String requestUri) {

        switch (mode) {
        case INCLUDE_MAPPINGS:
            if (!findMatchingPath(metricsPathLabelGroupings, requestUri).isPresent()) {
                return false;
            }
            break;
        case INCLUDE:
            if (!findMatchingPath(includes, requestUri).isPresent()) {
                return false;
            }
            break;
        case EXCLUDE:
            if (findMatchingPath(excludes, requestUri).isPresent()) {
                return false;
            }
            break;
        default:
            break;
        }
        return true;
    }

    public Optional<String> groupUrl(String requestUri) {
        return findMatchingPath(metricsPathLabelGroupings, requestUri);
    }

    private Optional<String> findMatchingPath(Map<String, String> mappings, String url) {

        return mappings.entrySet().stream()
            .filter(e -> url.matches(e.getValue()))
            .map(Map.Entry::getKey)
            .findFirst();

    }

    public enum MetricsMode {
        ALL,
        INCLUDE_MAPPINGS,
        INCLUDE,
        EXCLUDE
    }

}
