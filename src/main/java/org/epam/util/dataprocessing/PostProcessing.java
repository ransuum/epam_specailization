package org.epam.util.dataprocessing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingType;
import org.epam.models.request.TrainingRequest;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainerRepository;
import org.epam.repository.TrainingRepository;
import org.epam.util.CredentialsGenerator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

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

@Component
@PropertySource("classpath:application.properties")
public class PostProcessing implements BeanPostProcessor {
    private static final Log log = LogFactory.getLog(PostProcessing.class);

    @Value("${data.trainee.file}")
    private Resource traineeFile;

    @Value("${data.training-request.file}")
    private Resource trainingRequestFile;

    @Value("${data.trainer.file}")
    private Resource trainerFile;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("storageMap")) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Map<Integer, Object>> storageMap = (Map<String, Map<Integer, Object>>) bean;

                List<Trainer> trainers = loadTrainers();
                for (int i = 0; i < trainers.size(); i++) {
                    storageMap.get("trainers").put(i + 1, trainers.get(i));
                }

                List<Trainee> trainees = loadTrainees();
                for (int i = 0; i < trainees.size(); i++) {
                    storageMap.get("trainees").put(i + 1, trainees.get(i));
                }

                List<TrainingRequest> trainingRequests = loadTrainingRequests();
                Map<Integer, Trainee> traineeMap = new HashMap<>();
                Map<Integer, Trainer> trainerMap = new HashMap<>();

                storageMap.get("trainees").forEach((id, trainee) -> traineeMap.put(id, (Trainee) trainee));
                storageMap.get("trainers").forEach((id, trainer) -> trainerMap.put(id, (Trainer) trainer));

                int trainingId = 1;
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

                        storageMap.get("trainings").put(trainingId++, training);
                    }
                }

                log.info("Storage map initialized successfully via post processor");
            } catch (Exception e) {
                log.error("Error initializing storage map in post processor: " + e.getMessage(), e);
            }
        }
        return bean;
    }

    private List<Trainee> loadTrainees() throws IOException {
        List<Trainee> trainees = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(traineeFile.getInputStream()))) {
            String line;
            if ((line = reader.readLine()) != null && line.startsWith("address")) {}
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
            if ((line = reader.readLine()) != null && line.startsWith("specialization")) {}
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

    private List<TrainingRequest> loadTrainingRequests() throws IOException {
        List<TrainingRequest> requests = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(trainingRequestFile.getInputStream()))) {
            String line;
            if ((line = reader.readLine()) != null && line.startsWith("id")) {}
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
