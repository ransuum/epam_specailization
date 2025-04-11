package org.epam.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMetrics {
    private final Counter requestCounter;
    private final Counter errorCounter;
    private final Timer requestLatency;

    public ApplicationMetrics(MeterRegistry registry) {
        this.requestCounter = Counter.builder("app.requests.total")
                .description("Total number of requests")
                .register(registry);

        this.errorCounter = Counter.builder("app.errors.total")
                .description("Total number of errors")
                .register(registry);

        this.requestLatency = Timer.builder("app.request.latency")
                .description("Request latency in milliseconds")
                .register(registry);
    }

    public void incrementRequestCount() {
        this.requestCounter.increment();
    }

    public void incrementErrorCount() {
        this.errorCounter.increment();
    }

    public Timer getRequestLatencyTimer() {
        return this.requestLatency;
    }
}
