package org.epam.repository;


import org.epam.exception.NotFoundException;
import org.epam.models.entity.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository extends CrudRepository<String, TrainingType> {
    TrainingType save(TrainingType trainingType);

    Optional<TrainingType> findById(String id);

    void delete(String id) throws NotFoundException;

    List<TrainingType> findAll();

    TrainingType update(String id, TrainingType trainingType);
}
