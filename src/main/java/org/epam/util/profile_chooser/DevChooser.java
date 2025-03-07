package org.epam.util.profile_chooser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.enums.Profile;
import org.epam.service.TraineeService;
import org.epam.service.TrainerService;
import org.epam.service.TrainingService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DevChooser implements Chooser {
    private static final Log log = LogFactory.getLog(DevChooser.class);
    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingService trainingService;
    private AnnotationConfigApplicationContext context;

    @Override
    public void initialize(AnnotationConfigApplicationContext context) {
        this.context = context;
        this.traineeService = context.getBean(TraineeService.class);
        this.trainerService = context.getBean(TrainerService.class);
        this.trainingService = context.getBean(TrainingService.class);
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
    public Profile getProfile() {
        return Profile.DEV;
    }
}
