package org.epam.service.impl;

import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingListDto;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingTypeName;
import org.epam.models.request.create.TrainingRequestCreate;
import org.epam.models.request.update.TraineeTrainingRequestUpdate;
import org.epam.models.request.update.TrainerTrainingRequestUpdate;
import org.epam.models.request.update.TrainingRequestUpdate;
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

import static org.epam.utils.CheckerField.check;

@Service
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    public TrainingServiceImpl(TrainingRepository trainingRepository, TraineeRepository traineeRepository,
                               TrainingTypeRepository trainingTypeRepository, TrainerRepository trainerRepository) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    @Transactional
    public TrainingDto save(TrainingRequestCreate trainingCreationData) throws NotFoundException {
        var trainer = trainerRepository.findByUsername(trainingCreationData.trainerUsername())
                .orElseThrow(() -> new NotFoundException("Trainer not found"));
        var trainee = traineeRepository.findByUsername(trainingCreationData.traineeUsername())
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        return TrainingMapper.INSTANCE.toDto(trainingRepository.save(
                Training.builder()
                        .trainer(trainer)
                        .trainee(trainee)
                        .trainingName(trainingCreationData.trainingName())
                        .startTime(LocalDate.parse(trainingCreationData.startTime(), formatter))
                        .duration(trainingCreationData.duration())
                        .build())
        );
    }

    @Override
    public TrainingDto update(String id, TrainingRequestUpdate trainingUpdateData) throws NotFoundException {
        var training = trainingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Training not found"));

        if (check(trainingUpdateData.traineeUsername()))
            training.setTrainee(traineeRepository.findByUsername(trainingUpdateData.traineeUsername())
                    .orElseThrow(() -> new NotFoundException("Trainee not found")));

        if (check(trainingUpdateData.trainerUsername()))
            training.setTrainer(trainerRepository.findByUsername(trainingUpdateData.trainerUsername())
                    .orElseThrow(() -> new NotFoundException("Trainer not found")));

        if (check(trainingUpdateData.trainingTypeId()))
            training.setTrainingType(trainingTypeRepository.findById(trainingUpdateData.trainingTypeId())
                    .orElseThrow(() -> new NotFoundException("Training type not found")));

        if (check(trainingUpdateData.trainingName())) training.setTrainingName(trainingUpdateData.trainingName());
        if (check(trainingUpdateData.duration())) training.setDuration(trainingUpdateData.duration());
        return TrainingMapper.INSTANCE.toDto(trainingRepository.save(training));
    }

    @Override
    public void delete(String id) throws NotFoundException {
        trainingRepository.delete(id);

    }

    @Override
    public List<TrainingListDto> findAll() {
        return trainingRepository.findAll()
                .stream()
                .map(TrainingMapper.INSTANCE::toListDto)
                .toList();
    }

    @Override
    public TrainingDto findById(String id) throws NotFoundException {
        return TrainingMapper.INSTANCE.toDto(trainingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainee not found by id " + id)));
    }

    @Override
    public List<TrainingListDto.TrainingListDtoForUser> getTraineeTrainings(String username, String fromDate,
                                                                            String toDate, String trainerName,
                                                                            TrainingTypeName trainingTypeName) {
        return trainingRepository.getTraineeTrainings(username,
                        check(fromDate) ? LocalDate.parse(fromDate, formatter) : null,
                        check(toDate) ? LocalDate.parse(toDate, formatter) : null,
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
                        check(fromDate) ? LocalDate.parse(fromDate, formatter) : null,
                        check(toDate) ? LocalDate.parse(toDate, formatter) : null,
                        traineeName,
                        trainingTypeName)
                .stream()
                .map(TrainingMapper.INSTANCE::toListDtoForTrainer)
                .toList();
    }

    @Override
    public List<TrainingDto> updateTrainingsOfTrainee(String traineeUsername, List<TraineeTrainingRequestUpdate> trainingUpdateData) throws NotFoundException {
        var trainee = traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        return trainingUpdateData.stream()
                .map(traineeTrainingRequestUpdate
                        -> TrainingMapper.INSTANCE.toDto(trainingRepository.save(
                        Training.builder()
                                .trainingName(traineeTrainingRequestUpdate.trainingName())
                                .trainer(trainerRepository.findByUsername(traineeTrainingRequestUpdate.trainerUsername())
                                        .orElseThrow(() -> new NotFoundException("Trainer not found")))
                                .trainee(trainee)
                                .trainingType(trainingTypeRepository.findByTrainingTypeName(
                                                TrainingTypeName.getTrainingNameFromString(traineeTrainingRequestUpdate.trainingTypeName()))
                                        .orElseThrow(() -> new NotFoundException("Training type not found")))
                                .duration(traineeTrainingRequestUpdate.duration())
                                .startTime(LocalDate.parse(traineeTrainingRequestUpdate.startTime(), formatter))
                                .build()))
                ).toList();
    }

    @Override
    public List<TrainingDto> updateTrainingsOfTrainer(String trainerUsername, List<TrainerTrainingRequestUpdate> trainingUpdateData) throws NotFoundException {
        var trainer = trainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new NotFoundException("Trainer not found"));

        return trainingUpdateData.stream()
                .map(trainerTrainingRequestUpdate
                        -> TrainingMapper.INSTANCE.toDto(trainingRepository.save(
                        Training.builder()
                                .trainingName(trainerTrainingRequestUpdate.trainingName())
                                .trainer(trainer)
                                .trainee(traineeRepository.findByUsername(trainerTrainingRequestUpdate.traineeUsername())
                                        .orElseThrow(() -> new NotFoundException("Trainee not found")))
                                .trainingType(trainingTypeRepository.findByTrainingTypeName(
                                                TrainingTypeName.getTrainingNameFromString(trainerTrainingRequestUpdate.trainingTypeName()))
                                        .orElseThrow(() -> new NotFoundException("Training type not found")))
                                .duration(trainerTrainingRequestUpdate.duration())
                                .startTime(LocalDate.parse(trainerTrainingRequestUpdate.startTime(), formatter))
                                .build()))
                ).toList();
    }
}
