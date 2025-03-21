package org.epam.service.impl;

import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingDtoForTrainee;
import org.epam.models.dto.TrainingDtoForTrainer;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingType;
import org.epam.models.request.trainingrequest.TrainingRequestCreate;
import org.epam.models.request.trainingrequest.TrainingRequestUpdate;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainerRepository;
import org.epam.repository.TrainingRepository;
import org.epam.repository.TrainingViewRepository;
import org.epam.service.TrainingService;
import org.epam.utils.mappers.TrainingMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static org.epam.utils.CheckerField.check;

@Service
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingViewRepository trainingViewRepository;

    public TrainingServiceImpl(TrainingRepository trainingRepository, TraineeRepository traineeRepository,
                               TrainingViewRepository trainingViewRepository, TrainerRepository trainerRepository) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingViewRepository = trainingViewRepository;
    }

    @Override
    public TrainingDto save(TrainingRequestCreate request) throws NotFoundException {
        var trainer = trainerRepository.findById(request.trainerId())
                .orElseThrow(() -> new NotFoundException("Trainer not found"));
        var trainee = traineeRepository.findById(request.traineeId())
                .orElseThrow(() -> new NotFoundException("Trainee not found"));
        var trainingView = trainingViewRepository.findById(request.trainingViewId())
                .orElseThrow(() -> new NotFoundException("Training view not found"));

        return TrainingMapper.INSTANCE.toDto(trainingRepository.save(
                Training.builder()
                        .trainer(trainer)
                        .trainee(trainee)
                        .trainingType(trainingView)
                        .trainingName(request.trainingName())
                        .startTime(request.startTime())
                        .duration(request.duration())
                        .build())
        );
    }

    @Override
    public TrainingDto update(String id, TrainingRequestUpdate request) throws NotFoundException {
        var training = trainingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Training not found"));

        if (check(request.traineeId()))
            training.setTrainee(traineeRepository.findById(request.traineeId())
                    .orElseThrow(() -> new NotFoundException("Trainee not found")));

        if (check(request.trainerId()))
            training.setTrainer(trainerRepository.findById(request.trainerId())
                    .orElseThrow(() -> new NotFoundException("Trainer not found")));

        if (check(request.trainingViewId()))
            training.setTrainingType(trainingViewRepository.findById(request.trainingViewId())
                    .orElseThrow(() -> new NotFoundException("Training view not found")));

        if (check(request.trainingName())) training.setTrainingName(request.trainingName());
        if (check(request.duration())) training.setDuration(request.duration());
        return TrainingMapper.INSTANCE.toDto(trainingRepository.save(training));
    }

    @Override
    public void delete(String id) throws NotFoundException {
        trainingRepository.delete(id);

    }

    @Override
    public List<TrainingDto> findAll() {
        return trainingRepository.findAll()
                .stream()
                .map(TrainingMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public TrainingDto findById(String id) throws NotFoundException {
        return TrainingMapper.INSTANCE.toDto(trainingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainee not found by id " + id)));
    }

    @Override
    public List<TrainingDtoForTrainee> findTrainingWithUsernameOfTrainee(String username, LocalDate fromDate,
                                                                         LocalDate toDate, String trainerName,
                                                                         TrainingType trainingType) {
        return trainingRepository.findTrainingWithUsernameOfTrainee(username, fromDate, toDate, trainerName, trainingType)
                .stream()
                .map(TrainingMapper.INSTANCE::toDtoForTrainee)
                .toList();
    }

    @Override
    public List<TrainingDtoForTrainer> findTrainingWithUsernameOfTrainer(String username, LocalDate fromDate,
                                                                         LocalDate toDate, String traineeName,
                                                                         TrainingType trainingType) {
        return trainingRepository.findTrainingWithUsernameOfTrainer(username, fromDate, toDate, traineeName, trainingType)
                .stream()
                .map(TrainingMapper.INSTANCE::toDtoForTrainer)
                .toList();
    }
}
