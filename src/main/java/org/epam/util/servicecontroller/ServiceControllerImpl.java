package org.epam.util.servicecontroller;

import org.epam.service.TraineeService;
import org.epam.service.TrainerService;
import org.epam.service.TrainingService;
import org.springframework.stereotype.Component;

@Component
public class ServiceControllerImpl implements ServiceController {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public ServiceControllerImpl(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @Override
    public TraineeService getTraineeService() {
        return traineeService;
    }

    @Override
    public TrainerService getTrainerService() {
        return trainerService;
    }

    @Override
    public TrainingService getTrainingService() {
        return trainingService;
    }
}
