package org.epam.transaction.configuration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.extern.log4j.Log4j2;
import org.epam.transaction.logging.TransactionContext;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class TransactionExecution {

    private final EntityManager entityManager;

    public TransactionExecution(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T> T executeWithTransaction(DatabaseOperation<T> operation) {
        String transactionId = TransactionContext.getTransactionId();
        EntityTransaction transaction = entityManager.getTransaction();

        log.info("[TX:{}] Transaction started" , transactionId);

        try {
            transaction.begin();
            T result = operation.execute();
            log.info("[TX:{}] Transaction process with {}", transactionId, result.getClass().getName());
            transaction.commit();
            log.info("[TX:{}] Transaction committed successfully with {}", transactionId, result.getClass().getName());
            return result;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
                log.warn("[TX:{}] Transaction rolled back due to error: {}", transactionId, e.getMessage());
            }
            log.error("[TX:{}] Transaction failed: {}", transactionId, e.getMessage(), e);
            throw e;
        } finally {
            TransactionContext.clear();
        }
    }

    public void executeWithTransaction(VoidDatabaseOperation operation) {
        String transactionId = TransactionContext.getTransactionId();
        EntityTransaction transaction = entityManager.getTransaction();

        log.info("[TX:{}] Void Transaction started", transactionId);

        try {
            transaction.begin();
            operation.execute();
            transaction.commit();
            log.info("[TX:{}] Void Transaction committed successfully", transactionId);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
                log.warn("[TX:{}] Void Transaction rolled back due to error: {}", transactionId, e.getMessage());
            }
            log.error("[TX:{}] Transaction failed: {}", transactionId, e.getMessage(), e);
            throw e;
        } finally {
            TransactionContext.clear();
        }
    }
}
