package org.epam.repository.configuration;

import java.util.List;
import java.util.Optional;

public interface SpringTaskRepository<I, T> {
    T save(T t);
    T update(T t);
    void delete(I i);
    List<T> findAll();
    Optional<T> findById(I i);
}
