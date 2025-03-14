package org.epam.util.subcontroller;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Trainer;
import org.epam.models.enums.TrainingType;
import org.epam.models.request.TrainingRequest;
import org.epam.util.CredentialsGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.epam.util.CheckerField.check;

@Getter
@Service
public class SubControllerMenu {
    private static final Log log = LogFactory.getLog(SubControllerMenu.class);
    public static final Set<String> existingUsernames = new HashSet<>();
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public Trainee createTrainee(Scanner scanner) {
        System.out.print("Enter address: ");
        String address = scanner.next();
        System.out.print("Enter first name: ");
        String firstName = scanner.next();
        System.out.print("Enter last name: ");
        String lastName = scanner.next();
        System.out.print("Enter date of birth(dd-MM-yyyy): ");
        String dateOfBirth = scanner.next();
        String username = CredentialsGenerator.generateUsername(firstName, lastName);
        existingUsernames.add(username);
        String password = CredentialsGenerator.generatePassword(username);

        return new Trainee(address, LocalDate.parse(dateOfBirth, dateTimeFormatter), firstName, lastName, username, password, Boolean.TRUE);
    }

    public Trainer createTrainer(Scanner scanner) {
        System.out.print("Enter specialization: ");
        String specialization = scanner.next();
        System.out.print("Enter first name: ");
        String firstName = scanner.next();
        System.out.print("Enter last name: ");
        String lastName = scanner.next();
        String username = CredentialsGenerator.generateUsername(firstName, lastName);
        existingUsernames.add(username);
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
            System.out.print("Enter training date(dd-MM-yyyy): ");
            String trainingDate = scanner.next();


            return TrainingRequest.builder()
                    .traineeId(traineeId)
                    .trainerId(trainerId)
                    .trainingName(trainingName)
                    .trainingType(trainingType)
                    .trainingDate(LocalDate.parse(trainingDate, dateTimeFormatter))
                    .trainingDuration(duration)
                    .build();
        } catch (Exception e) {
            log.info("Error creating training: " + e.getMessage());
            return null;
        }
    }

    public Pair<Integer, Trainee> updateTrainee(Scanner scanner) {
        try {
            System.out.print("Enter trainee id to update: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter new address (leave blank for no change): ");
            String address = scanner.nextLine();

            System.out.print("Enter new first name (leave blank for no change): ");
            String firstName = scanner.nextLine();

            System.out.print("Enter new last name (leave blank for no change): ");
            String lastName = scanner.nextLine();

            System.out.print("Enter date of birth(dd-MM-yyyy): ");
            String dateOfBirth = scanner.nextLine();


            return Pair.of(id, new Trainee(address, LocalDate.parse(dateOfBirth, dateTimeFormatter), firstName, lastName, Boolean.FALSE));
        } catch (Exception e) {
            log.info("Error updating trainee: " + e.getMessage());
            return null;
        }
    }

    public Pair<Integer, Trainer> updateTrainer(Scanner scanner) {
        try {
            System.out.print("Enter trainer id to update: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter new specialization (leave blank for no change): ");
            String specialization = scanner.nextLine();

            System.out.print("Enter new first name (leave blank for no change): ");
            String firstName = scanner.nextLine();

            System.out.print("Enter new last name (leave blank for no change): ");
            String lastName = scanner.nextLine();

            return Pair.of(id, new Trainer(specialization, firstName, lastName, Boolean.TRUE));
        } catch (Exception e) {
            log.info("Error updating trainer: " + e.getMessage());
            return null;
        }
    }

    public Pair<Integer, TrainingRequest> updateTraining(Scanner scanner) {
        try {
            System.out.print("Enter training id to update: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter new trainee id (leave blank for no change): ");
            String traineeIdStr = scanner.nextLine();
            Integer traineeId = traineeIdStr.trim().isEmpty() ? null : Integer.valueOf(traineeIdStr);

            System.out.print("Enter training date(dd-MM-yyyy): ");
            String trainingDate = scanner.nextLine();
            LocalDate localDate = LocalDate.parse(trainingDate, dateTimeFormatter);

            System.out.print("Enter new trainer id (leave blank for no change): ");
            String trainerIdStr = scanner.nextLine();
            Integer trainerId = trainerIdStr.trim().isEmpty() ? null : Integer.valueOf(trainerIdStr);

            System.out.print("Enter new training name (leave blank for no change): ");
            String trainingName = scanner.nextLine();
            trainingName = trainingName.trim().isEmpty() ? null : trainingName;

            System.out.print("Enter new training type (leave blank for no change): ");
            String typeStr = scanner.nextLine();
            TrainingType trainingType = null;
            if (check(typeStr)) {
                try {
                    trainingType = TrainingType.valueOf(typeStr.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid training type. Valid options are: " +
                            Arrays.toString(TrainingType.values()));
                }
            }

            System.out.print("Enter new training duration (in minutes) (leave blank for no change): ");
            String durationStr = scanner.nextLine();
            Integer duration = durationStr.trim().isEmpty() ? null : Integer.valueOf(durationStr);

            return Pair.of(id, TrainingRequest.builder()
                    .traineeId(traineeId)
                    .trainerId(trainerId)
                    .trainingName(trainingName)
                    .trainingType(trainingType)
                    .trainingDuration(duration)
                    .trainingDate(localDate)
                    .build());
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
