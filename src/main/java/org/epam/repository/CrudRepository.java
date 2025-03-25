package org.epam.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<I, O> {
    O save(O item);
    Optional<O> findById(I id);
    void delete(I id);
    List<O> findAll();
    O update(I id, O item);
}
