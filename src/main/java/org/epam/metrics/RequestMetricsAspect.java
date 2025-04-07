package org.epam.metrics;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.epam.exception.MetricsException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RequestMetricsAspect {
    private final ApplicationMetrics metrics;

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object measureRequestMetrics(ProceedingJoinPoint joinPoint) {
        metrics.incrementRequestCount();

        try {
            return metrics.getRequestLatencyTimer().record(() -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable e) {
                    metrics.incrementErrorCount();
                    throw new MetricsException(e.getMessage());
                }
            });
        } catch (Exception e) {
            metrics.incrementErrorCount();
            throw e;
        }
    }
}