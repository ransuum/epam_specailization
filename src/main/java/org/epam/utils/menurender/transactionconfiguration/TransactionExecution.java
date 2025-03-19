package org.epam.utils.menurender.transactionconfiguration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class TransactionExecution {
    private static final Logger log = LogManager.getLogger(TransactionExecution.class);

    private final EntityManager entityManager;

    public TransactionExecution(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T> T executeWithTransaction(DatabaseOperation<T> operation) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            T result = operation.execute();
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Transaction failed for transaction: {}", e.getMessage());
            throw e;
        }
    }

    public void executeVoidWithTransaction(VoidDatabaseOperation operation) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            operation.execute();
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Transaction failed for void transaction: {}", e.getMessage());
            throw e;
        }
    }
}
