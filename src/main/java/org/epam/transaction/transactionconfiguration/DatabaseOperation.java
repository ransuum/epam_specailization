package org.epam.transaction.transactionconfiguration;

public interface DatabaseOperation<T> {
    T execute();
}
