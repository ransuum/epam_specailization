package org.epam.service;


import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingDtoForTrainee;
import org.epam.models.dto.TrainingDtoForTrainer;
import org.epam.models.enums.TrainingName;
import org.epam.models.request.create.TrainingRequestCreate;
import org.epam.models.request.update.TrainingRequestUpdate;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {
    TrainingDto save(TrainingRequestCreate request) throws NotFoundException;

    TrainingDto update(String id, TrainingRequestUpdate request) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TrainingDto> findAll();

    TrainingDto findById(String id) throws NotFoundException;

    List<TrainingDtoForTrainee> findTrainingWithUsernameOfTrainee(String username, String fromDate,
                                                                  String toDate, String trainerName,
                                                                  TrainingName trainingName);

    List<TrainingDtoForTrainer>  findTrainingWithUsernameOfTrainer(String username, String fromDate,
                                                                   String toDate, String traineeName,
                                                                   TrainingName trainingName);

    List<TrainingDto> addTrainingsToTrainee(String traineeId, List<TrainingRequestCreate> requests) throws NotFoundException;
}
