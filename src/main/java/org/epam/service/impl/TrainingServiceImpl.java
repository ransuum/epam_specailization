package org.epam.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.dto.TrainingDto;
import org.epam.models.entity.Training;
import org.epam.models.request.TrainingRequest;
import org.epam.service.TrainingService;
import org.epam.repository.TraineeRepo;
import org.epam.repository.TrainerRepo;
import org.epam.repository.TrainingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.util.CheckerField.check;

@Service
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepo trainingRepo;
    private final TrainerRepo trainerRepo;
    private final TraineeRepo traineeRepo;
    private final ObjectMapper objectMapper;
    private static final Log log = LogFactory.getLog(TrainingServiceImpl.class);

    @Autowired
    public TrainingServiceImpl(TrainingRepo trainingRepo, TrainerRepo trainerRepo, TraineeRepo traineeRepo, ObjectMapper objectMapper) {
        this.trainingRepo = trainingRepo;
        this.trainerRepo = trainerRepo;
        this.traineeRepo = traineeRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    public TrainingDto save(TrainingRequest t) {
        log.info("Saving training...");
        var trainee = traineeRepo.findById(t.traineeId())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        var trainer = trainerRepo.findById(t.trainerId())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        Training training = new Training(
                trainee,
                trainer,
                t.trainingName(), t.trainingType(), t.trainingDate(), t.trainingDuration());

        return objectMapper.convertValue(trainingRepo.save(training), TrainingDto.class);
    }

    @Override
    public TrainingDto update(Integer id, TrainingRequest t) {
        log.info("Update training...");
        Training training = trainingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        if (check(String.valueOf(t.trainingType()))) training.setTrainingType(t.trainingType());
        if (check(t.trainingName())) training.setTrainingName(t.trainingName());
        if (check(t.trainingDuration())) training.setTrainingDuration(t.trainingDuration());
        if (check(t.trainingDate())) training.setTrainingDate(t.trainingDate());
        if (check(t.traineeId())) training.setTrainee(traineeRepo.findById(t.traineeId())
                .orElseThrow(() -> new RuntimeException("Trainee not found")));
        if (check(t.trainerId())) training.setTrainer(trainerRepo.findById(t.trainerId())
                .orElseThrow(() -> new RuntimeException("Trainer not found")));
        return objectMapper.convertValue(trainingRepo.update(training), TrainingDto.class);
    }

    @Override
    public void delete(Integer id) {
        log.info("delete training...");
        trainingRepo.delete(findById(id).id());
    }

    @Override
    public List<TrainingDto> findAll() {
        return trainingRepo.findAll()
                .stream()
                .map(training -> objectMapper.convertValue(training, TrainingDto.class))
                .toList();
    }

    @Override
    public TrainingDto findById(Integer id) {
        return objectMapper.convertValue(trainingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Training not found")), TrainingDto.class);
    }
}
