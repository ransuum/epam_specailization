package org.epam.transaction.configuration;

public interface DatabaseOperation<T> {
    T execute();
}
