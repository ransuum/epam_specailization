package org.epam.service;


import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingListDto;
import org.epam.models.enums.TrainingName;
import org.epam.models.request.create.TrainingRequestCreate;
import org.epam.models.request.update.TrainingRequestUpdate;

import java.util.List;

public interface TrainingService {
    TrainingDto save(TrainingRequestCreate request) throws NotFoundException;

    TrainingDto update(String id, TrainingRequestUpdate request) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TrainingListDto> findAll();

    TrainingDto findById(String id) throws NotFoundException;

    List<TrainingListDto.TrainingListDtoForUser> getTraineeTrainings(String username, String fromDate,
                                                                     String toDate, String trainerName,
                                                                     TrainingName trainingName);

    List<TrainingListDto.TrainingListDtoForUser> getTrainerTrainings(String username, String fromDate,
                                                                     String toDate, String traineeName,
                                                                     TrainingName trainingName);

    List<TrainingDto> addTrainingsToTrainee(String traineeId, List<TrainingRequestCreate> requests) throws NotFoundException;
}
