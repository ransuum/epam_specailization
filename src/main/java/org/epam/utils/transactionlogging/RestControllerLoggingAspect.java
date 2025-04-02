package org.epam.utils.transactionlogging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Log4j2
public class RestControllerLoggingAspect {

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logRestCall(ProceedingJoinPoint joinPoint) throws Throwable {
        var transactionId = TransactionContext.getTransactionId();
        var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("[TX:{}] REST Request: {} {} - Parameters: {}",
                transactionId,
                request.getMethod(),
                request.getRequestURI(),
                Arrays.toString(joinPoint.getArgs()));

        try {
            var result = joinPoint.proceed();

            if (result instanceof ResponseEntity<?> response)
                log.info("[TX:{}] REST Response: {} - Body: {}",
                        transactionId,
                        response.getStatusCode(),
                        response.getBody());

            return result;
        } catch (Exception e) {
            log.error("[TX:{}] REST Error: {} - {}",
                    transactionId,
                    e.getClass().getSimpleName(),
                    e.getMessage());
            throw e;
        }
    }
}
