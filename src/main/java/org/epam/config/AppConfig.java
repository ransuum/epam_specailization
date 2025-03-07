package org.epam.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingType;
import org.epam.repository.TraineeRepo;
import org.epam.repository.TrainerRepo;
import org.epam.repository.TrainingRepo;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan(value = "org.epam")
public class AppConfig {
    private final TraineeRepo traineeRepo;
    private final TrainingRepo trainingRepo;
    private final TrainerRepo trainerRepo;
    private static final Log log = LogFactory.getLog(AppConfig.class);

    private final List<Trainee> trainees = List.of(
            new Trainee("address", LocalDate.now(), "Valerii", "Dmitrenko", "valerii", "Password123@", Boolean.TRUE),
            new Trainee("address", LocalDate.now(), "Dima", "Dmitrenko", "Dima", "Dima@", Boolean.TRUE),
            new Trainee("address", LocalDate.now(), "Artem", "Dmitrenko", "Artem", "Artem@", Boolean.TRUE),
            new Trainee("address", LocalDate.now(), "Maria", "Dmitrenko", "Maria", "Maria@", Boolean.TRUE),
            new Trainee("address", LocalDate.now(), "John", "Dmitrenko", "John", "John@", Boolean.TRUE),
            new Trainee("address", LocalDate.now(), "Valerii", "Dmitrenko", "valerii", "Password123@", Boolean.TRUE),
            new Trainee("address", LocalDate.now(), "Valerii", "Dmitrenko", "valerii", "Password123@", Boolean.TRUE),
            new Trainee("address", LocalDate.now(), "Valerii", "Dmitrenko", "valerii", "Password123@", Boolean.TRUE));

    private final List<Trainer> trainers = List.of(
            new Trainer("Fitness", "Alex", "Johnson", "alex.j", "Fitness123@", Boolean.TRUE),
            new Trainer("Yoga", "Sarah", "Miller", "sarah.m", "Yoga456@", Boolean.TRUE),
            new Trainer("Crossfit", "Mike", "Thompson", "mike.t", "Cross789@", Boolean.TRUE),
            new Trainer("Boxing", "Anna", "Lee", "anna.l", "Box101@", Boolean.TRUE),
            new Trainer("Nutrition", "Robert", "Wilson", "robert.w", "Nutri202@", Boolean.TRUE),
            new Trainer("Yoga", "Sarah", "Miller", "sarah.m", "Yoga456@", Boolean.TRUE),
            new Trainer("Crossfit", "Mike", "Thompson", "mike.t", "Cross789@", Boolean.TRUE),
            new Trainer("Boxing", "Anna", "Lee", "anna.l", "Box101@", Boolean.TRUE),
            new Trainer("Nutrition", "Robert", "Wilson", "robert.w", "Nutri202@", Boolean.TRUE));

    public AppConfig(TraineeRepo traineeRepo, TrainingRepo trainingRepo, TrainerRepo trainerRepo) {
        this.traineeRepo = traineeRepo;
        this.trainingRepo = trainingRepo;
        this.trainerRepo = trainerRepo;
    }

    @Bean
    public ApplicationListener<ContextRefreshedEvent> traineeListener() {
        return event -> {
            try {
                List<Trainee> savedTrainees = new ArrayList<>();
                for (Trainee trainee : trainees)
                    savedTrainees.add(traineeRepo.save(trainee));


                List<Trainer> savedTrainers = new ArrayList<>();
                for (Trainer trainer : trainers)
                    savedTrainers.add(trainerRepo.save(trainer));

                if (!savedTrainees.isEmpty() && !savedTrainers.isEmpty()){
                    for (int i = 0; i < 8; i++) {
                        trainingRepo.save(new Training(
                                trainees.get(i),
                                trainers.get(i),
                                "Training", TrainingType.CAMPUS, LocalDate.now(), 365));
                    }
                }


            } catch (Exception e) {
                log.info("Error: " + e.getMessage());
            }
        };
    }
}
