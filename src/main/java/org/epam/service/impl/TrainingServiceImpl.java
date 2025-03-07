package org.epam.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.entity.Training;
import org.epam.models.request.TrainingRequest;
import org.epam.service.TrainingService;
import org.epam.repository.TraineeRepo;
import org.epam.repository.TrainerRepo;
import org.epam.repository.TrainingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepo trainingRepo;
    private final TrainerRepo trainerRepo;
    private final TraineeRepo traineeRepo;
    private static final Log log = LogFactory.getLog(TrainingServiceImpl.class);

    @Autowired
    public TrainingServiceImpl(TrainingRepo trainingRepo, TrainerRepo trainerRepo, TraineeRepo traineeRepo) {
        this.trainingRepo = trainingRepo;
        this.trainerRepo = trainerRepo;
        this.traineeRepo = traineeRepo;
    }

    @Override
    public Training save(TrainingRequest t) {
        log.info("Saving training...");
        return trainingRepo.save(new Training(
                traineeRepo.findById(t.getTraineeId())
                        .orElseThrow(() -> new RuntimeException("Trainee not found")),
                trainerRepo.findById(t.getTrainerId())
                        .orElseThrow(() -> new RuntimeException("Trainer not found")),
                t.getTrainingName(), t.getTrainingType(), t.getTrainingDate(), t.getTrainingDuration()));
    }

    @Override
    public Training update(TrainingRequest t) {
        log.info("Update training...");
        Training training = findById(t.getId());
        if (t.getTrainingType() != null) training.setTrainingType(t.getTrainingType());
        if (t.getTrainingName() != null) training.setTrainingName(t.getTrainingName());
        if (t.getTrainingDuration() != null) training.setTrainingDuration(t.getTrainingDuration());
        if (t.getTrainingDate() != null) training.setTrainingDate(t.getTrainingDate());
        if (t.getTraineeId() != null)
            training.setTrainee(traineeRepo.findById(t.getTraineeId())
                    .orElseThrow(() -> new RuntimeException("Trainee not found")));
        if (t.getTrainerId() != null)
            training.setTrainer(trainerRepo.findById(t.getTrainerId())
                    .orElseThrow(() -> new RuntimeException("Trainer not found")));
        return trainingRepo.update(training);
    }

    @Override
    public void delete(Integer id) {
        log.info("delete training...");
        trainingRepo.delete(findById(id).getId());
    }

    @Override
    public List<Training> findAll() {
        return trainingRepo.findAll();
    }

    @Override
    public Training findById(Integer id) {
        return trainingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Training not found"));
    }
}
