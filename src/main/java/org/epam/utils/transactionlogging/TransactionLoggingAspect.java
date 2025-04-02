package org.epam.utils.transactionlogging;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class TransactionLoggingAspect {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object logTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        var transactionId = TransactionContext.getTransactionId();
        var methodName = joinPoint.getSignature().getName();
        var className = joinPoint.getTarget().getClass().getSimpleName();

        log.info("[TX:{}] Starting transaction in {}.{}()",
                transactionId, className, methodName);

        try {
            Object result = joinPoint.proceed();
            log.info("[TX:{}] Transaction completed successfully in {}.{}()",
                    transactionId, className, methodName);
            return result;
        } catch (Exception e) {
            log.error("[TX:{}] Transaction failed in {}.{}() - {}",
                    transactionId, className, methodName, e.getMessage());
            throw e;
        } finally {
            TransactionContext.clear();
        }
    }
}
