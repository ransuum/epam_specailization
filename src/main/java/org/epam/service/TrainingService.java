package org.epam.service;


import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingDtoForTrainee;
import org.epam.models.dto.TrainingDtoForTrainer;
import org.epam.models.enums.TrainingType;
import org.epam.models.request.trainingrequest.TrainingRequestCreate;
import org.epam.models.request.trainingrequest.TrainingRequestUpdate;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {
    TrainingDto save(TrainingRequestCreate request) throws NotFoundException;

    TrainingDto update(String id, TrainingRequestUpdate request) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TrainingDto> findAll();

    TrainingDto findById(String id) throws NotFoundException;

    List<TrainingDtoForTrainee> findTrainingWithUsernameOfTrainee(String username, LocalDate fromDate,
                                                                  LocalDate toDate, String trainerName,
                                                                  TrainingType trainingType);

    List<TrainingDtoForTrainer>  findTrainingWithUsernameOfTrainer(String username, LocalDate fromDate,
                                                                   LocalDate toDate, String traineeName,
                                                                   TrainingType trainingType);
}
