package ske.aurora.prometheus;

import java.util.Set;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.hotspot.ThreadExports;
import ske.aurora.prometheus.collector.HttpMetricsCollector;
import ske.aurora.prometheus.collector.JvmGcMetrics;
import ske.aurora.prometheus.collector.LogbackMetricsAppender;
import ske.aurora.prometheus.collector.Operation;

public final class MetricsConfig {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MetricsConfig.class);

    private MetricsConfig() {

    }

    public static CollectorRegistry init(Set<HttpMetricsCollector> httpCollectors) {
        CollectorRegistry registry = CollectorRegistry.defaultRegistry;
        registry.clear();

        httpCollectors.forEach(it -> it.register(registry));

        //do not register the default metrics since we want full control here.
        new StandardExports().register(registry);
        new MemoryPoolsExports().register(registry);
        new ThreadExports().register(registry);

        new JvmGcMetrics().register(registry);
        Operation.getInstance().register(registry);

        // logback metrics
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        //cannot use instrumented appender here since it is not possible to send in the registry
        LogbackMetricsAppender appender = new LogbackMetricsAppender(registry);
        appender.setContext(lc);
        appender.start();
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(appender);

        logger.debug("Registered standard, memory, thread, gc, httpcollectors and logback metrics");
        return registry;
    }
}
