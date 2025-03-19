package org.epam.repository;


import org.epam.models.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends CrudRepository<String, Trainer> {
    Trainer save(Trainer trainer);

    Optional<Trainer> findById(String id);

    void delete(String id);

    List<Trainer> findAll();

    Trainer update(String id, Trainer trainer);

    Optional<Trainer> findByUsername(String username);


}
