package org.epam.utils.transactionconfiguration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class TransactionExecution {

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
            if (transaction.isActive()) transaction.rollback();
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
            if (transaction.isActive()) transaction.rollback();
            log.error("Transaction failed for void transaction: {}", e.getMessage());
            throw e;
        }
    }
}
