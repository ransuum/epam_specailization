package org.epam.util.profilechooser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.enums.Profile;
import org.epam.service.TraineeService;
import org.epam.service.TrainerService;
import org.epam.service.TrainingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DevChooser implements Chooser {
    private static final Log log = LogFactory.getLog(DevChooser.class);
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private AnnotationConfigApplicationContext context;

    @Value("${spring.active.profile}")
    private String profile;

    public DevChooser(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }


    @Override
    public void initialize(AnnotationConfigApplicationContext context) {
        this.context = context;
        log.info("Initializing application with DevChooser");
        process();
    }

    @Override
    public void process() {
        log.info("Trainee list: ");
        traineeService.findAll().forEach(System.out::println);
        log.info("Trainer list: ");
        trainerService.findAll().forEach(System.out::println);
        log.info("Trainings list: {}");
        trainingService.findAll().forEach(System.out::println);

        context.close();
    }

    @Override
    public boolean getProfile() {
        return profile.equals(Profile.dev.name());
    }
}
