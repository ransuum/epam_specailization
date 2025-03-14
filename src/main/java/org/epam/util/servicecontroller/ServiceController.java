package org.epam.util.servicecontroller;

import org.epam.service.TraineeService;
import org.epam.service.TrainerService;
import org.epam.service.TrainingService;

public interface ServiceController {
    TraineeService getTraineeService();
    TrainerService getTrainerService();
    TrainingService getTrainingService();
}
