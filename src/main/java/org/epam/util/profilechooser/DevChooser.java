package org.epam.util.profilechooser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.enums.Profile;
import org.epam.util.servicecontroller.ServiceController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DevChooser implements Chooser {
    private static final Log log = LogFactory.getLog(DevChooser.class);
    private final ServiceController serviceController;

    @Value("${spring.profiles.active}")
    private String profile;

    public DevChooser(ServiceController serviceController) {
        this.serviceController = serviceController;
    }

    @Override
    public void initialize() {
        log.info("Initializing application with DevChooser");
        process();
    }

    @Override
    public void process() {
        log.info("Trainee list: ");
        serviceController.getTraineeService().findAll().forEach(System.out::println);
        log.info("Trainer list: ");
        serviceController.getTrainerService().findAll().forEach(System.out::println);
        log.info("Trainings list: {}");
        serviceController.getTrainingService().findAll().forEach(System.out::println);
    }

    @Override
    public boolean getProfile() {
        return profile.equals(Profile.dev.name());
    }
}
