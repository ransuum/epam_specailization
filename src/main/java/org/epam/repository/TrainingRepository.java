package org.epam.repository;


import org.epam.exception.NotFoundException;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends CrudRepository<String, Training> {
    Training save(Training training);

    Optional<Training> findById(String id);

    void delete(String id) throws NotFoundException;

    List<Training> findAll();

    Training update(String id, Training training);

    List<Training> findTrainingWithUsernameOfTrainee(String username, LocalDate fromDate,
                                                     LocalDate toDate, String trainerName,
                                                     TrainingType trainingType);

    List<Training> findTrainingWithUsernameOfTrainer(String username, LocalDate fromDate,
                                                     LocalDate toDate, String traineeName,
                                                     TrainingType trainingType);
}
