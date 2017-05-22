package ske.aurora.prometheus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class HttpMetricsCollectorConfig {

    private MetricsMode mode = MetricsMode.ALL;
    private LinkedHashMap<String, Pattern> metricsPathLabelGroupings = new LinkedHashMap<>();
    private LinkedHashMap<String, Pattern> includes = new LinkedHashMap<>();
    private LinkedHashMap<String, Pattern> excludes = new LinkedHashMap<>();

    public HttpMetricsCollectorConfig() {
        //For spring
    }

    public HttpMetricsCollectorConfig(MetricsMode mode,
        LinkedHashMap<String, String> metricsPathLabelGroupings,
        LinkedHashMap<String, String> includes,
        LinkedHashMap<String, String> excludes) {
        this.mode = mode;

        this.metricsPathLabelGroupings = compilePatterns(metricsPathLabelGroupings);
        this.includes = compilePatterns(includes);
        this.excludes = compilePatterns(excludes);
    }

    private LinkedHashMap<String, Pattern> compilePatterns(LinkedHashMap<String, String> inputMap) {
        LinkedHashMap<String, Pattern> patternMap = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : inputMap.entrySet()) {
            patternMap.put(entry.getKey(), Pattern.compile(entry.getValue()));
        }
        return patternMap;
    }

    public Map<String, Pattern> getMetricsPathLabelGroupings() {
        return metricsPathLabelGroupings;
    }

    public MetricsMode getMode() {
        return mode;
    }

    public void setMode(MetricsMode mode) {
        this.mode = mode;
    }

    public Map<String, Pattern> getIncludes() {
        return includes;
    }

    public Map<String, Pattern> getExcludes() {
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

    private Optional<String> findMatchingPath(Map<String, Pattern> mappings, String url) {

        return mappings.entrySet().stream()
            .filter(e -> e.getValue().matcher(url).find())
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
