package org.epam.service.impl;

import lombok.RequiredArgsConstructor;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingListDto;
import org.epam.models.entity.Training;
import org.epam.models.enums.NotFoundMessages;
import org.epam.models.enums.TrainingTypeName;
import org.epam.models.dto.create.TrainingCreateDto;
import org.epam.models.dto.update.TraineeTrainingUpdateDto;
import org.epam.models.dto.update.TrainerTrainingUpdateDto;
import org.epam.models.dto.update.TrainingUpdateDto;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainerRepository;
import org.epam.repository.TrainingRepository;
import org.epam.repository.TrainingTypeRepository;
import org.epam.service.TrainingService;
import org.epam.utils.mappers.TrainingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.epam.utils.CheckerBuilder.check;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    @Transactional
    public TrainingDto save(TrainingCreateDto trainingCreationData) throws NotFoundException {
        var trainer = trainerRepository.findByUsers_Username(trainingCreationData.trainerUsername())
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal()));
        var trainee = traineeRepository.findByUsers_Username(trainingCreationData.traineeUsername())
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINEE.getVal()));
        var trainingType = trainingTypeRepository.findByTrainingTypeName(
                        TrainingTypeName.getTrainingNameFromString(trainingCreationData.trainingTypeName()))
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINING_TYPE.getVal()));

        return TrainingMapper.INSTANCE.toDto(trainingRepository.save(
                Training.builder()
                        .trainer(trainer)
                        .trainee(trainee)
                        .trainingType(trainingType)
                        .trainingName(trainingCreationData.trainingName())
                        .startTime(LocalDate.parse(trainingCreationData.startTime(), FORMATTER))
                        .duration(trainingCreationData.duration())
                        .build())
        );
    }

    @Override
    @Transactional
    public TrainingDto update(String id, TrainingUpdateDto trainingUpdateData) throws NotFoundException {
        var training = trainingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Training not found"));

        if (check(trainingUpdateData.traineeUsername()))
            training.setTrainee(traineeRepository.findByUsers_Username(trainingUpdateData.traineeUsername())
                    .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINEE.getVal())));

        if (check(trainingUpdateData.trainerUsername()))
            training.setTrainer(trainerRepository.findByUsers_Username(trainingUpdateData.trainerUsername())
                    .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal())));

        if (check(trainingUpdateData.trainingTypeId()))
            training.setTrainingType(trainingTypeRepository.findById(trainingUpdateData.trainingTypeId())
                    .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINING_TYPE.getVal())));

        if (check(trainingUpdateData.trainingName())) training.setTrainingName(trainingUpdateData.trainingName());
        if (check(trainingUpdateData.duration())) training.setDuration(trainingUpdateData.duration());
        return TrainingMapper.INSTANCE.toDto(trainingRepository.save(training));
    }

    @Override
    @Transactional
    public void delete(String id) throws NotFoundException {
        var training = trainingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINING.getVal()));
        trainingRepository.delete(training);
    }

    @Override
    public List<TrainingListDto> findAll() {
        return trainingRepository.findAll()
                .stream()
                .map(TrainingMapper.INSTANCE::toListDto)
                .toList();
    }

    @Override
    @Transactional
    public TrainingDto findById(String id) throws NotFoundException {
        return TrainingMapper.INSTANCE.toDto(trainingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainee not found by id " + id)));
    }

    @Override
    public List<TrainingListDto.TrainingListDtoForUser> getTraineeTrainings(String username, String fromDate,
                                                                            String toDate, String trainerName,
                                                                            TrainingTypeName trainingTypeName) {
        return trainingRepository.getTraineeTrainings(username,
                        check(fromDate) ? LocalDate.parse(fromDate, FORMATTER) : null,
                        check(toDate) ? LocalDate.parse(toDate, FORMATTER) : null,
                        trainerName,
                        trainingTypeName)
                .stream()
                .map(TrainingMapper.INSTANCE::toListDtoForTrainee)
                .toList();
    }

    @Override
    public List<TrainingListDto.TrainingListDtoForUser> getTrainerTrainings(String username, String fromDate,
                                                                            String toDate, String traineeName,
                                                                            TrainingTypeName trainingTypeName) {
        return trainingRepository.getTrainerTrainings(username,
                        check(fromDate) ? LocalDate.parse(fromDate, FORMATTER) : null,
                        check(toDate) ? LocalDate.parse(toDate, FORMATTER) : null,
                        traineeName,
                        trainingTypeName)
                .stream()
                .map(TrainingMapper.INSTANCE::toListDtoForTrainer)
                .toList();
    }

    @Override
    public List<TrainingDto> updateTrainingsOfTrainee(String traineeUsername, List<TraineeTrainingUpdateDto> trainingUpdateData) throws NotFoundException {
        var trainee = traineeRepository.findByUsers_Username(traineeUsername)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        return trainingUpdateData.stream()
                .map(traineeTrainingUpdateDto
                        -> TrainingMapper.INSTANCE.toDto(trainingRepository.save(
                        Training.builder()
                                .trainingName(traineeTrainingUpdateDto.trainingName())
                                .trainer(trainerRepository.findByUsers_Username(traineeTrainingUpdateDto.trainerUsername())
                                        .orElseThrow(() -> new NotFoundException("Trainer not found")))
                                .trainee(trainee)
                                .trainingType(trainingTypeRepository.findByTrainingTypeName(
                                                TrainingTypeName.getTrainingNameFromString(traineeTrainingUpdateDto.trainingTypeName()))
                                        .orElseThrow(() -> new NotFoundException("Training type not found")))
                                .duration(traineeTrainingUpdateDto.duration())
                                .startTime(LocalDate.parse(traineeTrainingUpdateDto.startTime(), FORMATTER))
                                .build()))
                ).toList();
    }

    @Override
    public List<TrainingDto> updateTrainingsOfTrainer(String trainerUsername, List<TrainerTrainingUpdateDto> trainingUpdateData) throws NotFoundException {
        var trainer = trainerRepository.findByUsers_Username(trainerUsername)
                .orElseThrow(() -> new NotFoundException("Trainer not found"));

        return trainingUpdateData.stream()
                .map(trainerTrainingUpdateDto
                        -> TrainingMapper.INSTANCE.toDto(trainingRepository.save(
                        Training.builder()
                                .trainingName(trainerTrainingUpdateDto.trainingName())
                                .trainer(trainer)
                                .trainee(traineeRepository.findByUsers_Username(trainerTrainingUpdateDto.traineeUsername())
                                        .orElseThrow(() -> new NotFoundException("Trainee not found")))
                                .trainingType(trainingTypeRepository.findByTrainingTypeName(
                                                TrainingTypeName.getTrainingNameFromString(trainerTrainingUpdateDto.trainingTypeName()))
                                        .orElseThrow(() -> new NotFoundException("Training type not found")))
                                .duration(trainerTrainingUpdateDto.duration())
                                .startTime(LocalDate.parse(trainerTrainingUpdateDto.startTime(), FORMATTER))
                                .build()))
                ).toList();
    }
}
