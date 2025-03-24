package org.epam.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingDtoForTrainee;
import org.epam.models.dto.TrainingDtoForTrainer;
import org.epam.models.enums.TrainingName;
import org.epam.models.request.create.TrainingRequestUpdate;
import org.epam.service.TrainingService;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import static org.epam.utils.CheckerField.check;

@Controller
public class TrainingController {
    private static final Logger logger = LogManager.getLogger(TrainingController.class);
    private final TrainingService trainingService;
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private List<TrainingRequestUpdate> setTrainingRequestCreateList(String userId) {
        return List.of(
                new TrainingRequestUpdate(userId, "uuid3", "FIRST NAME", "type1", LocalDate.now(), 50L),
                new TrainingRequestUpdate(userId, "uuid4", "FIRST NAME", "type2", LocalDate.now(), 75L));
    }

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    public TrainingDto create(Scanner scanner) {
        try {
            System.out.print("Enter trainee id: ");
            var traineeId = scanner.next().trim();
            System.out.print("Enter trainer id: ");
            var trainerId = scanner.next().trim();
            scanner.nextLine();
            System.out.print("Enter training name: ");
            var trainingName = scanner.nextLine().trim();
            System.out.print("Enter training trainingView id: ");
            var trainingViewId = scanner.next().trim();
            System.out.print("Enter start time date(dd-MM-yyyy): ");
            var date = scanner.next().trim();
            System.out.print("Enter duration: ");
            var duration = scanner.nextLong();

            return trainingService.save(new TrainingRequestUpdate(traineeId, trainerId, trainingName,
                    trainingViewId, LocalDate.parse(date, formatter), duration));
        } catch (Exception e) {
            logger.error("Error creating training: {}", e.getMessage());
            return null;
        }
    }

    public void findAll() {
        try {
            trainingService.findAll().forEach(System.out::println);
        } catch (Exception e) {
            logger.error("Error retrieving all trainings: {}", e.getMessage());
        }
    }

    public TrainingDto findById(Scanner scanner) {
        try {
            System.out.print("Enter training id: ");
            var trainingId = scanner.next();
            return trainingService.findById(trainingId);
        } catch (Exception e) {
            logger.error("Error finding training by id: {}", e.getMessage());
            return null;
        }
    }

    public TrainingDto update(Scanner scanner) {
        try {
            System.out.print("Enter training id: ");
            var trainingId = scanner.next().trim();
            scanner.nextLine();
            System.out.print("Enter trainer id: ");
            var trainerId = scanner.nextLine().trim();
            System.out.print("Enter trainee id: ");
            var traineeId = scanner.nextLine().trim();
            System.out.print("Enter training name: ");
            var trainingName = scanner.nextLine().trim();
            System.out.print("Enter training view id: ");
            var trainingViewId = scanner.nextLine().trim();
            System.out.print("Enter start time date(dd-MM-yyyy): ");
            var date = scanner.nextLine().trim();
            System.out.print("Enter duration: ");
            var duration = Long.valueOf(scanner.nextLine());

            return trainingService.update(trainingId, new org.epam.models.request.update.TrainingRequestUpdate(traineeId, trainerId, trainingName,
                    trainingViewId, LocalDate.parse(date, formatter), duration));
        } catch (Exception e) {
            logger.error("Error updating training: {}", e.getMessage());
            return null;
        }
    }

    public void delete(Scanner scanner) {
        try {
            System.out.print("Enter training id: ");
            var trainingId = scanner.next().trim();
            trainingService.delete(trainingId);
        } catch (Exception e) {
            logger.error("Error deleting training: {}", e.getMessage());
        }
    }

    public List<TrainingDtoForTrainee> findTrainingWithUsernameOfTrainee(Scanner scanner) {
        System.out.print("Enter trainee's username: ");
        var username = scanner.next().trim();
        scanner.nextLine();
        System.out.print("From date: ");
        var fromDate = scanner.nextLine().trim();
        System.out.print("To date: ");
        var toDate = scanner.nextLine().trim();
        System.out.print("Trainer name: ");
        var trainerName = scanner.nextLine().trim();
        System.out.print("Training type: ");
        var trainingType = scanner.nextLine().trim();
        return trainingService.findTrainingWithUsernameOfTrainee(username,
                check(fromDate) ? LocalDate.parse(fromDate, formatter) : null,
                check(toDate) ? LocalDate.parse(toDate, formatter) : null,
                trainerName,
                TrainingName.valueOf(trainingType.toUpperCase()));
    }

    public List<TrainingDtoForTrainer> findTrainingWithUsernameOfTrainer(Scanner scanner) {
        System.out.print("Enter trainer's username: ");
        var username = scanner.next().trim();
        scanner.nextLine();
        System.out.print("From date: ");
        var fromDate = scanner.nextLine().trim();
        System.out.print("To date: ");
        var toDate = scanner.nextLine().trim();
        System.out.print("Trainee name: ");
        var traineeName = scanner.nextLine().trim();
        System.out.print("Training type: ");
        var trainingType = scanner.nextLine().trim();
        return trainingService.findTrainingWithUsernameOfTrainer(username,
                check(fromDate) ? LocalDate.parse(fromDate, formatter) : null,
                check(toDate) ? LocalDate.parse(toDate, formatter) : null,
                traineeName,
                TrainingName.valueOf(trainingType.toUpperCase()));
    }

    public List<TrainingDto> addListToTrainee(String userId) {
        return trainingService.addTrainingsToTrainee(userId, setTrainingRequestCreateList(userId));
    }
}
