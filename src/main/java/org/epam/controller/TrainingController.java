package org.epam.controller;

import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingDtoForTrainee;
import org.epam.models.dto.TrainingDtoForTrainer;
import org.epam.models.enums.TrainingType;
import org.epam.models.request.trainingrequest.TrainingRequestCreate;
import org.epam.models.request.trainingrequest.TrainingRequestUpdate;
import org.epam.service.TrainingService;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import static org.epam.utils.CheckerField.check;

@Controller
public class TrainingController {
    private final TrainingService trainingService;
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    public TrainingDto create(Scanner scanner) {
        System.out.print("Enter trainee id: ");
        var traineeId = scanner.next().trim();
        System.out.print("Enter trainer id: ");
        var trainerId = scanner.next().trim();
        System.out.print("Enter training name: ");
        var trainingName = scanner.next().trim();
        System.out.print("Enter training trainingView id: ");
        var trainingViewId = scanner.next().trim();
        System.out.print("Enter start time date(dd-MM-yyyy): ");
        var date = scanner.next().trim();
        System.out.print("Enter duration: ");
        var duration = scanner.nextLong();
        return trainingService.save(new TrainingRequestCreate(traineeId, trainerId, trainingName,
                trainingViewId, LocalDate.parse(date, formatter), duration));
    }

    public void findAll() {
        trainingService.findAll().forEach(System.out::println);
    }

    public TrainingDto findById(Scanner scanner) {
        System.out.print("Enter training id: ");
        var trainingId = scanner.next();
        return trainingService.findById(trainingId);
    }

    public TrainingDto update(Scanner scanner) {
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
        return trainingService.update(trainingId, new TrainingRequestUpdate(traineeId, trainerId, trainingName,
                trainingViewId, LocalDate.parse(date, formatter), duration));
    }

    public void delete(Scanner scanner) {
        System.out.print("Enter training id: ");
        var trainingId = scanner.next().trim();
        trainingService.delete(trainingId);
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
                TrainingType.valueOf(trainingType.toUpperCase()));
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
                TrainingType.valueOf(trainingType.toUpperCase()));
    }
}
