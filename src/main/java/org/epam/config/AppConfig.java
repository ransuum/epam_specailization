package org.epam.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingType;
import org.epam.models.request.TrainingRequest;
import org.epam.repository.TraineeRepo;
import org.epam.repository.TrainerRepo;
import org.epam.repository.TrainingRepo;
import org.epam.util.CredentialsGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.epam.util.subcontroller.SubControllerMenu.existingUsernames;

@Configuration
@ComponentScan(value = "org.epam")
@PropertySource("classpath:application.properties")
public class AppConfig {
    private static final Log log = LogFactory.getLog(AppConfig.class);

    @Value("${data.trainee.file}")
    private Resource traineeFile;

    @Value("${data.training-request.file}")
    private Resource trainingRequestFile;

    @Value("${data.trainer.file}")
    private Resource trainerFile;

    @Bean
    public Map<String, Map<Integer, Object>> storageMap() {
        Map<String, Map<Integer, Object>> storage = new HashMap<>();
        storage.put("trainees", new HashMap<>());
        storage.put("trainers", new HashMap<>());
        storage.put("trainings", new HashMap<>());
        return storage;
    }

    @Bean
    public ApplicationListener<ContextRefreshedEvent> dataInitializer(TraineeRepo traineeRepo, TrainingRepo trainingRepo, TrainerRepo trainerRepo) {
        return event -> {
            try {
                List<Trainee> trainees = loadTrainees();
                List<Trainer> trainers = loadTrainers();
                List<TrainingRequest> trainingRequests = loadTrainingRequests();

                Map<Integer, Trainee> traineeMap = new HashMap<>();
                for (int i = 0; i < trainees.size(); i++) {
                    Trainee savedTrainee = traineeRepo.save(trainees.get(i));
                    traineeMap.put(i + 1, savedTrainee);
                }

                Map<Integer, Trainer> trainerMap = new HashMap<>();
                for (int i = 0; i < trainers.size(); i++) {
                    Trainer savedTrainer = trainerRepo.save(trainers.get(i));
                    trainerMap.put(i + 1, savedTrainer);
                }

                if (!traineeMap.isEmpty() && !trainerMap.isEmpty() && !trainingRequests.isEmpty()) {
                    for (TrainingRequest request : trainingRequests) {
                        Trainee trainee = traineeMap.get(request.traineeId());
                        Trainer trainer = trainerMap.get(request.trainerId());

                        if (trainee != null && trainer != null) {
                            Training training = new Training(
                                    trainee,
                                    trainer,
                                    request.trainingName(),
                                    request.trainingType(),
                                    request.trainingDate(),
                                    request.trainingDuration());

                            trainingRepo.save(training);
                        }
                    }
                }

                log.info("Successfully initialized data from files");
            } catch (Exception e) {
                log.error("Error initializing data: " + e.getMessage(), e);
            }
        };

    }

    private List<Trainee> loadTrainees() throws IOException {
        List<Trainee> trainees = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(traineeFile.getInputStream()))) {
            String line;
            if ((line = reader.readLine()) != null && line.startsWith("address")) {
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String firstName = parts[2].trim();
                    String lastName = parts[3].trim();
                    String username = CredentialsGenerator.generateUsername(firstName, lastName);
                    existingUsernames.add(username);
                    Trainee trainee = new Trainee(
                            parts[0].trim(),
                            LocalDate.parse(parts[1].trim(), formatter),
                            firstName,
                            lastName,
                            username,
                            CredentialsGenerator.generatePassword(username),
                            Boolean.parseBoolean(parts[4].trim())
                    );
                    trainees.add(trainee);
                }
            }
        }

        return trainees;
    }

    private List<Trainer> loadTrainers() throws IOException {
        List<Trainer> trainers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(trainerFile.getInputStream()))) {
            String line;
            if ((line = reader.readLine()) != null && line.startsWith("specialization")) {
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String firstName = parts[1].trim();
                    String lastName = parts[2].trim();
                    String username = CredentialsGenerator.generateUsername(firstName, lastName);
                    existingUsernames.add(username);
                    Trainer trainer = new Trainer(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            username,
                            CredentialsGenerator.generatePassword(username),
                            Boolean.parseBoolean(parts[3].trim())
                    );
                    trainers.add(trainer);
                }
            }
        }

        return trainers;
    }

    @Bean
    public ObjectMapper beanMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }


    private List<TrainingRequest> loadTrainingRequests() throws IOException {
        List<TrainingRequest> requests = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(trainingRequestFile.getInputStream()))) {
            String line;
            if ((line = reader.readLine()) != null && line.startsWith("id")) {
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    TrainingRequest request = TrainingRequest.builder()
                            .traineeId(Integer.parseInt(parts[0].trim()))
                            .trainerId(Integer.parseInt(parts[1].trim()))
                            .trainingName(parts[2].trim())
                            .trainingType(TrainingType.valueOf(parts[3].trim()))
                            .trainingDate(LocalDate.parse(parts[4].trim(), formatter))
                            .trainingDuration(Integer.parseInt(parts[5].trim()))
                            .build();
                    requests.add(request);
                }
            }
        }

        return requests;
    }
}
