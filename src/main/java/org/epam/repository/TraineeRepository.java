package org.epam.repository;

import org.epam.models.entity.Trainee;

import java.util.Optional;

public interface TraineeRepository extends CrudRepository<Integer, Trainee> {
    Optional<Trainee> findById(Integer integer);

    Trainee save(Trainee trainee);

    Trainee update(Trainee trainee);

    void delete(Integer id);
}
