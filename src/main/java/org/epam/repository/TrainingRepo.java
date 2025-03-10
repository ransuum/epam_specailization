package org.epam.repository;

import org.epam.models.entity.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingRepo extends CrudRepository<Integer, Training> {
    Training save(Training training);

    Training update(Training training);

    void delete(Integer integer);

    List<Training> findAll();

    Optional<Training> findById(Integer integer);
}
