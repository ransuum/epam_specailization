package org.epam.transaction.transactionlogging;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.epam.models.dto.AuthDto;
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
                Arrays.toString(maskSensitiveData(joinPoint.getArgs())));

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

    private Object[] maskSensitiveData(Object[] args) {
        if (args == null) return new Object[0];

        Object[] maskedArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof AuthDto auth) {
                maskedArgs[i] = new AuthDto(auth.username(), "********");
            } else {
                maskedArgs[i] = args[i];
            }
        }
        return maskedArgs;
    }
}
