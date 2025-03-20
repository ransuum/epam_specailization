package org.epam.repository;


import org.epam.exception.NotFoundException;
import org.epam.models.entity.TrainingView;

import java.util.List;
import java.util.Optional;

public interface TrainingViewRepository extends CrudRepository<String, TrainingView> {
    TrainingView save(TrainingView trainingView);

    Optional<TrainingView> findById(String id);

    void delete(String id) throws NotFoundException;

    List<TrainingView> findAll();

    TrainingView update(String id, TrainingView trainingView);
}
