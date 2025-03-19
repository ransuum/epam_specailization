package org.epam.repository;


import org.epam.models.entity.Trainee;

import java.util.Optional;

public interface TraineeRepository extends CrudRepository<String, Trainee> {
    Trainee save(Trainee trainee);

    Optional<Trainee> findById(String id);

    void delete(String id);

    Trainee update(String id, Trainee trainee);

    Optional<Trainee> findByUsername(String username);

    String deleteByUsername(String username);
}
