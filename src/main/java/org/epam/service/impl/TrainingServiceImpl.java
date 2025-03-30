package org.epam.service.impl;

import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingListDto;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingName;
import org.epam.models.request.create.TrainingRequestCreate;
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
    public TrainingDto save(TrainingRequestCreate request) throws NotFoundException {
        var trainer = trainerRepository.findById(request.trainerId())
                .orElseThrow(() -> new NotFoundException("Trainer not found"));
        var trainee = traineeRepository.findById(request.traineeId())
                .orElseThrow(() -> new NotFoundException("Trainee not found"));
        var trainingView = trainingTypeRepository.findById(request.trainingViewId())
                .orElseThrow(() -> new NotFoundException("Training type not found"));

        return TrainingMapper.INSTANCE.toDto(trainingRepository.save(
                Training.builder()
                        .trainer(trainer)
                        .trainee(trainee)
                        .trainingType(trainingView)
                        .trainingName(request.trainingName())
                        .startTime(LocalDate.parse(request.startTime(), formatter))
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

        if (check(request.trainingTypeId()))
            training.setTrainingType(trainingTypeRepository.findById(request.trainingTypeId())
                    .orElseThrow(() -> new NotFoundException("Training type not found")));

        if (check(request.trainingName())) training.setTrainingName(request.trainingName());
        if (check(request.duration())) training.setDuration(request.duration());
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
                                                                            TrainingName trainingName) {
        return trainingRepository.getTraineeTrainings(username,
                        check(fromDate) ? LocalDate.parse(fromDate, formatter) : null,
                        check(toDate) ? LocalDate.parse(toDate, formatter) : null,
                        trainerName,
                        trainingName)
                .stream()
                .map(TrainingMapper.INSTANCE::toListDtoForTrainee)
                .toList();
    }

    @Override
    public List<TrainingListDto.TrainingListDtoForUser> getTrainerTrainings(String username, String fromDate,
                                                                            String toDate, String traineeName,
                                                                            TrainingName trainingName) {
        return trainingRepository.getTrainerTrainings(username,
                        check(fromDate) ? LocalDate.parse(fromDate, formatter) : null,
                        check(toDate) ? LocalDate.parse(toDate, formatter) : null,
                        traineeName,
                        trainingName)
                .stream()
                .map(TrainingMapper.INSTANCE::toListDtoForTrainer)
                .toList();
    }

    @Override
    public List<TrainingDto> addTrainingsToTrainee(String traineeId, List<TrainingRequestCreate> requests) throws NotFoundException {
        return requests.stream()
                .map(this::save)
                .toList();
    }
}
