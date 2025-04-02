package org.epam.utils.transactionconfiguration;

public interface DatabaseOperation<T> {
    T execute();
}
