package ske.aurora.prometheus.collector;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.Collector;
import io.prometheus.client.Gauge;

public final class Size extends Collector {

    private static final Logger logger = LoggerFactory.getLogger(Size.class);

    private static Size instance;

    private final Gauge sizes;

    public Size() {
        sizes = Gauge.build()
            .name("sizes")
            .help("Sizes of things")
            .labelNames("name", "type")
            .create();
    }

    public static void size(String name, int number) {
        instance.sizes.labels(name, "").set(number);
    }

    public static void size(String name, String type, int number) {
        instance.sizes.labels(name, type).set(number);
    }

    public static Size getInstance() {

        if (instance == null) {

            logger.debug("Create new size metrics");
            instance = new Size();
        }
        return instance;
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return sizes.collect();
    }

}
