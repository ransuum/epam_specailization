package org.epam.service;


import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingListDto;
import org.epam.models.enums.TrainingTypeName;
import org.epam.models.request.create.TrainingRequestCreate;
import org.epam.models.request.update.TraineeTrainingUpdateDto;
import org.epam.models.request.update.TrainerTrainingUpdateDto;
import org.epam.models.request.update.TrainingUpdateDto;

import java.util.List;

public interface TrainingService {
    TrainingDto save(TrainingRequestCreate request) throws NotFoundException;

    TrainingDto update(String id, TrainingUpdateDto trainingUpdateData) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TrainingListDto> findAll();

    TrainingDto findById(String id) throws NotFoundException;

    List<TrainingListDto.TrainingListDtoForUser> getTraineeTrainings(String username, String fromDate,
                                                                     String toDate, String trainerName,
                                                                     TrainingTypeName trainingTypeName);

    List<TrainingListDto.TrainingListDtoForUser> getTrainerTrainings(String username, String fromDate,
                                                                     String toDate, String traineeName,
                                                                     TrainingTypeName trainingTypeName);

    List<TrainingDto> updateTrainingsOfTrainee(String traineeUsername, List<TraineeTrainingUpdateDto> trainingUpdateData) throws NotFoundException;

    List<TrainingDto> updateTrainingsOfTrainer(String trainerUsername, List<TrainerTrainingUpdateDto> trainingUpdateData) throws NotFoundException;
}
