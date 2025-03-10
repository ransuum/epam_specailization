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
                traineeRepo.findById(t.traineeId())
                        .orElseThrow(() -> new RuntimeException("Trainee not found")),
                trainerRepo.findById(t.trainerId())
                        .orElseThrow(() -> new RuntimeException("Trainer not found")),
                t.trainingName(), t.trainingType(), t.trainingDate(), t.trainingDuration()));
    }

    @Override
    public Training update(Integer id, TrainingRequest t) {
        log.info("Update training...");
        Training training = findById(id);
        if (t.trainingType() != null) training.setTrainingType(t.trainingType());
        if (t.trainingName() != null) training.setTrainingName(t.trainingName());
        if (t.trainingDuration() != null) training.setTrainingDuration(t.trainingDuration());
        if (t.trainingDate() != null) training.setTrainingDate(t.trainingDate());
        if (t.traineeId() != null)
            training.setTrainee(traineeRepo.findById(t.traineeId())
                    .orElseThrow(() -> new RuntimeException("Trainee not found")));
        if (t.trainerId() != null)
            training.setTrainer(trainerRepo.findById(t.trainerId())
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
