package org.epam.repository;

import org.epam.models.entity.Trainer;
import org.epam.repository.configuration.SpringTaskRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerRepo extends SpringTaskRepository<Integer, Trainer> {
    Trainer save(Trainer trainer);

    Trainer update(Trainer trainer);

    void delete(Integer integer);

    List<Trainer> findAll();

    Optional<Trainer> findById(Integer integer);
}
