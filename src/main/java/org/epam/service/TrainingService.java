package org.epam.service;


import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingListDto;
import org.epam.models.enums.TrainingTypeName;
import org.epam.models.request.create.TrainingRequestCreate;
import org.epam.models.request.update.TraineeTrainingRequestUpdate;
import org.epam.models.request.update.TrainerTrainingRequestUpdate;
import org.epam.models.request.update.TrainingRequestUpdate;

import java.util.List;

public interface TrainingService {
    TrainingDto save(TrainingRequestCreate request) throws NotFoundException;

    TrainingDto update(String id, TrainingRequestUpdate trainingUpdateData) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TrainingListDto> findAll();

    TrainingDto findById(String id) throws NotFoundException;

    List<TrainingListDto.TrainingListDtoForUser> getTraineeTrainings(String username, String fromDate,
                                                                     String toDate, String trainerName,
                                                                     TrainingTypeName trainingTypeName);

    List<TrainingListDto.TrainingListDtoForUser> getTrainerTrainings(String username, String fromDate,
                                                                     String toDate, String traineeName,
                                                                     TrainingTypeName trainingTypeName);

    List<TrainingDto> updateTrainingsOfTrainee(String traineeUsername, List<TraineeTrainingRequestUpdate> trainingUpdateData) throws NotFoundException;

    List<TrainingDto> updateTrainingsOfTrainer(String trainerUsername, List<TrainerTrainingRequestUpdate> trainingUpdateData) throws NotFoundException;
}
