package ske.aurora.prometheus.collector;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.Collector;
import io.prometheus.client.Gauge;

public final class Status extends Collector {

    private static final Logger logger = LoggerFactory.getLogger(Status.class);

    private static Status instance;

    private final Gauge statuses;

    public Status() {
        statuses = Gauge.build()
            .name("statuses")
            .help("Status of things")
            .labelNames("name")
            .create();
    }

    public static void status(String name, StatusValue value) {

        instance.statuses.labels(name).set(value.getValue());
    }

    public static Status getInstance() {

        if (instance == null) {

            logger.debug("Create new statuses metrics");
            instance = new Status();
        }
        return instance;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return statuses.collect();
    }

    public enum StatusValue {

        OK(0),
        WARNING(1),
        CRITICAL(2),
        UNKNOWN(3);

        private int value;

        StatusValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
