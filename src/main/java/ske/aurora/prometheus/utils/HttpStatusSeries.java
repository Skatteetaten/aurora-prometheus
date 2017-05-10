package ske.aurora.prometheus.utils;

import java.util.Arrays;

public enum HttpStatusSeries {
    INFORMATIONAL(1),
    SUCCESSFUL(2),
    REDIRECTION(3),
    CLIENT_ERROR(4),
    SERVER_ERROR(5);

    public static final int HUNDRED = 100;
    private final int value;

    HttpStatusSeries(int value) {
        this.value = value;
    }

    public static HttpStatusSeries valueOf(int status) {

        int seriesCode = status / HUNDRED;
        return Arrays.asList(values()).stream()
            .filter(it -> it.value == seriesCode)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("No matching constant for [" + status + "]"));
    }

}
