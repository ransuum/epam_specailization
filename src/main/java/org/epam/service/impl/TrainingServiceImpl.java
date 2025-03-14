package org.epam.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.exception.EntityNotFoundException;
import org.epam.models.dto.TrainingDto;
import org.epam.models.entity.Training;
import org.epam.models.request.TrainingRequest;
import org.epam.service.TrainingService;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainerRepository;
import org.epam.repository.TrainingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.util.CheckerField.check;

@Service
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final ObjectMapper objectMapper;
    private static final Log log = LogFactory.getLog(TrainingServiceImpl.class);

    public TrainingServiceImpl(TrainingRepository trainingRepository, TrainerRepository trainerRepository, TraineeRepository traineeRepository, ObjectMapper objectMapper) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public TrainingDto save(TrainingRequest trainingRequest) {
        log.info("Saving training...");
        var trainee = traineeRepository.findById(trainingRequest.traineeId())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        var trainer = trainerRepository.findById(trainingRequest.trainerId())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        Training training = new Training(
                trainee,
                trainer,
                trainingRequest.trainingName(), trainingRequest.trainingType(),
                trainingRequest.trainingDate(), trainingRequest.trainingDuration());

        return objectMapper.convertValue(trainingRepository.save(training), TrainingDto.class);
    }

    @Override
    public TrainingDto update(Integer id, TrainingRequest trainingRequest) {
        log.info("Update training...");
        Training trainingById = trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training not found"));

        if (check(String.valueOf(trainingRequest.trainingType()))) trainingById.setTrainingType(trainingRequest.trainingType());
        if (check(trainingRequest.trainingName())) trainingById.setTrainingName(trainingRequest.trainingName());
        if (check(trainingRequest.trainingDuration())) trainingById.setTrainingDuration(trainingRequest.trainingDuration());
        if (check(trainingRequest.trainingDate())) trainingById.setTrainingDate(trainingRequest.trainingDate());
        if (check(trainingRequest.traineeId())) trainingById.setTrainee(traineeRepository.findById(trainingRequest.traineeId())
                .orElseThrow(() -> new RuntimeException("Trainee not found")));
        if (check(trainingRequest.trainerId())) trainingById.setTrainer(trainerRepository.findById(trainingRequest.trainerId())
                .orElseThrow(() -> new RuntimeException("Trainer not found")));
        return objectMapper.convertValue(trainingRepository.update(trainingById), TrainingDto.class);
    }

    @Override
    public void delete(Integer id) {
        log.info("delete training...");
        trainingRepository.delete(findById(id).id());
    }

    @Override
    public List<TrainingDto> findAll() {
        return trainingRepository.findAll()
                .stream()
                .map(training -> objectMapper.convertValue(training, TrainingDto.class))
                .toList();
    }

    @Override
    public TrainingDto findById(Integer id) {
        return objectMapper.convertValue(trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training not found")), TrainingDto.class);
    }
}
