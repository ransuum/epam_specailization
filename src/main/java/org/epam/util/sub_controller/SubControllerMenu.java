package org.epam.util.sub_controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Trainer;
import org.epam.models.enums.TrainingType;
import org.epam.models.request.TrainingRequest;
import org.epam.util.CredentialsGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Service
public class SubControllerMenu {
    private static final Log log = LogFactory.getLog(SubControllerMenu.class);
    private final Set<String> existingUsernames = new HashSet<>();

    public Trainee createTrainee(Scanner scanner) {
        System.out.print("Enter address: ");
        String address = scanner.next();
        System.out.print("Enter first name: ");
        String firstName = scanner.next();
        System.out.print("Enter last name: ");
        String lastName = scanner.next();
        String username = CredentialsGenerator.generateUsername(existingUsernames, firstName, lastName);
        String password = CredentialsGenerator.generatePassword(username);

        return new Trainee(address, LocalDate.now(), firstName, lastName, username, password, Boolean.TRUE);

    }

    public Trainer createTrainer(Scanner scanner) {
        System.out.print("Enter specialization: ");
        String specialization = scanner.next();
        System.out.print("Enter first name: ");
        String firstName = scanner.next();
        System.out.print("Enter last name: ");
        String lastName = scanner.next();
        String username = CredentialsGenerator.generateUsername(existingUsernames, firstName, lastName);
        String password = CredentialsGenerator.generatePassword(username);

        return new Trainer(specialization, firstName, lastName, username, password, Boolean.TRUE);
    }

    public TrainingRequest createTraining(Scanner scanner) {
        try {
            System.out.print("Enter trainee id: ");
            int traineeId = scanner.nextInt();
            System.out.print("Enter trainer id: ");
            int trainerId = scanner.nextInt();
            System.out.print("Enter training name: ");
            String trainingName = scanner.next();
            System.out.print("Enter training type (e.g., LABORATORY, THEORETICAL): ");
            String typeStr = scanner.next();
            TrainingType trainingType = TrainingType.valueOf(typeStr.toUpperCase());
            System.out.print("Enter training duration (in minutes): ");
            int duration = scanner.nextInt();

            return TrainingRequest.builder()
                    .traineeId(traineeId)
                    .trainerId(trainerId)
                    .trainingName(trainingName)
                    .trainingType(trainingType)
                    .trainingDate(LocalDate.now())
                    .trainingDuration(duration)
                    .build();
        } catch (Exception e) {
            log.info("Error creating training: " + e.getMessage());
            return null;
        }
    }

    public Trainee updateTrainee(Scanner scanner) {
        try {
            System.out.print("Enter trainee id to update: ");
            int id = scanner.nextInt();

            System.out.print("Enter new address (leave blank for no change): ");
            String address = scanner.nextLine();
            scanner.nextLine();
            System.out.print("Enter new first name (leave blank for no change): ");
            String firstName = scanner.nextLine();
            System.out.print("Enter new last name (leave blank for no change): ");
            String lastName = scanner.nextLine();
            String username = CredentialsGenerator.generateUsername(existingUsernames, firstName, lastName);
            return new Trainee(id, address, LocalDate.now(), firstName, lastName, username, CredentialsGenerator.generatePassword(username), Boolean.FALSE);
        } catch (Exception e) {
            log.info("Error updating trainee: " + e.getMessage());
            return null;
        }
    }

    public Trainer updateTrainer(Scanner scanner) {
        try {
            System.out.print("Enter trainer id to update: ");
            int id = scanner.nextInt();

            System.out.print("Enter new specialization (leave blank for no change): ");
            String specialization = scanner.nextLine();
            scanner.nextLine();
            System.out.print("Enter new first name (leave blank for no change): ");
            String firstName = scanner.nextLine();
            System.out.print("Enter new last name (leave blank for no change): ");
            String lastName = scanner.nextLine();
            String username = CredentialsGenerator.generateUsername(existingUsernames, firstName, lastName);

            return new Trainer(id, specialization, firstName, lastName, username, CredentialsGenerator.generatePassword(username), Boolean.TRUE);
        } catch (Exception e) {
            log.info("Error updating trainer: " + e.getMessage());
            return null;
        }
    }

    public TrainingRequest updateTraining(Scanner scanner) {
        try {
            System.out.print("Enter training id to update: ");
            int id = scanner.nextInt();

            System.out.print("Enter new trainee id (leave blank for no change): ");
            String traineeIdStr = scanner.nextLine();
            scanner.nextLine();

            System.out.print("Enter new trainer id (leave blank for no change): ");
            String trainerIdStr = scanner.nextLine();

            System.out.print("Enter new training name (leave blank for no change): ");
            String trainingName = scanner.nextLine();

            System.out.print("Enter new training type (LABORATORY, THEORETICAL) (leave blank for no change): ");
            String typeStr = scanner.nextLine();

            System.out.print("Enter new training duration (in minutes) (leave blank for no change): ");
            String durationStr = scanner.nextLine();

            return TrainingRequest.builder()
                    .id(id)
                    .traineeId(Integer.valueOf(traineeIdStr))
                    .trainerId(Integer.valueOf(trainerIdStr))
                    .trainingName(trainingName)
                    .trainingType(TrainingType.valueOf(typeStr))
                    .trainingDuration(Integer.valueOf(durationStr))
                    .build();
        } catch (Exception e) {
            log.info("Error updating training: " + e.getMessage());
            return null;
        }
    }

    public Integer deleteTrainee(Scanner scanner) {
        try {
            System.out.print("Enter trainee id to delete: ");
            return scanner.nextInt();
        } catch (Exception e) {
            log.info("Error deleting trainee: " + e.getMessage());
            return -1;
        }
    }

    public Integer deleteTrainer(Scanner scanner) {
        try {
            System.out.print("Enter trainer id to delete: ");
            return scanner.nextInt();
        } catch (Exception e) {
            log.info("Error deleting trainer: " + e.getMessage());
            return -1;
        }
    }

    public Integer deleteTraining(Scanner scanner) {
        try {
            System.out.print("Enter training id to delete: ");
            return scanner.nextInt();
        } catch (Exception e) {
            log.info("Error deleting training: " + e.getMessage());
            return -1;
        }
    }

    public Integer findTraineeById(Scanner scanner) {
        try {
            System.out.print("Enter trainee id to find: ");
            return scanner.nextInt();
        } catch (Exception e) {
            log.info("Error finding trainee: " + e.getMessage());
            return -1;
        }
    }

    public Integer findTrainerById(Scanner scanner) {
        try {
            System.out.print("Enter trainer id to find: ");
            return scanner.nextInt();
        } catch (Exception e) {
            log.info("Error finding trainer: " + e.getMessage());
            return -1;
        }
    }

    public Integer findTrainingById(Scanner scanner) {
        try {
            System.out.print("Enter training id to find: ");
            return scanner.nextInt();
        } catch (Exception e) {
            log.info("Error finding training: " + e.getMessage());
            return -1;
        }
    }

}
