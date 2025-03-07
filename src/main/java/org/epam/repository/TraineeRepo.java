package org.epam.repository;

import org.epam.models.entity.Trainee;
import org.epam.repository.configuration.SpringTaskRepository;

import java.util.Optional;

public interface TraineeRepo extends SpringTaskRepository<Integer, Trainee> {
    Optional<Trainee> findById(Integer integer);

    Trainee save(Trainee trainee);

    Trainee update(Trainee trainee);

    void delete(Integer id);
}
