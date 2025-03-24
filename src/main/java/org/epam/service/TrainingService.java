package org.epam.service;


import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingDtoForTrainee;
import org.epam.models.dto.TrainingDtoForTrainer;
import org.epam.models.enums.TrainingName;
import org.epam.models.request.create.TrainingRequestUpdate;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {
    TrainingDto save(TrainingRequestUpdate request) throws NotFoundException;

    TrainingDto update(String id, org.epam.models.request.update.TrainingRequestUpdate request) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TrainingDto> findAll();

    TrainingDto findById(String id) throws NotFoundException;

    List<TrainingDtoForTrainee> findTrainingWithUsernameOfTrainee(String username, LocalDate fromDate,
                                                                  LocalDate toDate, String trainerName,
                                                                  TrainingName trainingName);

    List<TrainingDtoForTrainer>  findTrainingWithUsernameOfTrainer(String username, LocalDate fromDate,
                                                                   LocalDate toDate, String traineeName,
                                                                   TrainingName trainingName);

    List<TrainingDto> addTrainingsToTrainee(String traineeId, List<TrainingRequestUpdate> requests) throws NotFoundException;
}
