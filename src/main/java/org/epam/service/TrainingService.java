package org.epam.service;


import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingListDto;
import org.epam.models.enums.TrainingTypeName;
import org.epam.models.dto.create.TrainingCreateDto;
import org.epam.models.dto.update.TraineeTrainingUpdateDto;
import org.epam.models.dto.update.TrainerTrainingUpdateDto;
import org.epam.models.dto.update.TrainingUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrainingService {
    TrainingDto save(TrainingCreateDto request) throws NotFoundException;

    TrainingDto update(String id, TrainingUpdateDto trainingUpdateData) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    Page<TrainingListDto> findAll(Pageable pageable);

    TrainingDto findById(String id) throws NotFoundException;

    Page<TrainingListDto.TrainingListDtoForUser> getTraineeTrainings(String username, String fromDate,
                                                                     String toDate, String trainerName,
                                                                     TrainingTypeName trainingTypeName,
                                                                     Pageable pageable);

    Page<TrainingListDto.TrainingListDtoForUser> getTrainerTrainings(String username, String fromDate,
                                                                     String toDate, String traineeName,
                                                                     TrainingTypeName trainingTypeName,
                                                                     Pageable pageable);

    List<TrainingDto> updateTrainingsOfTrainee(String traineeUsername, List<TraineeTrainingUpdateDto> trainingUpdateData) throws NotFoundException;

    List<TrainingDto> updateTrainingsOfTrainer(String trainerUsername, List<TrainerTrainingUpdateDto> trainingUpdateData) throws NotFoundException;
}
