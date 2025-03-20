package org.epam.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TraineeDto;
import org.epam.models.request.traineerequest.TraineeRequestCreate;
import org.epam.models.request.traineerequest.TraineeRequestUpdate;
import org.epam.service.TraineeService;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

@Controller
public class TraineeController {
    private final TraineeService traineeService;
    private static final Logger logger = LogManager.getLogger(TraineeController.class);
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    public TraineeDto addTrainee(String userid, Scanner scanner) {
        try {
            System.out.print("Enter your date of birth (dd-MM-yyyy): ");
            var dateOfBirth = scanner.next();
            System.out.print("Enter address: ");
            var address = scanner.next();
            return traineeService.save(new TraineeRequestCreate(userid, LocalDate.parse(dateOfBirth, formatter), address));
        } catch (Exception e) {
            logger.info("Error adding trainee: {}", e.getMessage());
            return null;
        }
    }

    public TraineeDto findById(String id) {
        try {
            return traineeService.findById(id);
        } catch (NotFoundException e) {
            logger.info("Trainee not found: {}", e.getMessage());
            return null;
        }
    }

    public void deleteById(Scanner scanner) {
        try {
            System.out.print("Enter id of trainee: ");
            String id = scanner.next();
            traineeService.delete(id);
        } catch (Exception e) {
            logger.info("Error deleting trainee: {}", e.getMessage());
        }
    }

    public TraineeDto updateTrainee(String id, Scanner scanner) {
        try {
            System.out.print("Enter user's id: ");
            var userId = scanner.nextLine().trim();
            System.out.print("Enter address: ");
            var address = scanner.nextLine().trim();
            System.out.print("Enter your date of birth (dd-MM-yyyy): ");
            var dateOfBirth = scanner.nextLine().trim();
            return traineeService.update(id, new TraineeRequestUpdate(userId, LocalDate.parse(dateOfBirth, formatter), address));
        } catch (Exception e) {
            logger.info("Error updating trainee: {}", e.getMessage());
            return null;
        }
    }

    public void findAll() {
        try {
            traineeService.findAll().forEach(System.out::println);
        } catch (Exception e) {
            logger.info("Error retrieving trainees: {}", e.getMessage());
        }
    }

    public TraineeDto changePassword(String id, Scanner scanner) {
        try {
            System.out.print("Enter old password: ");
            var oldPassword = scanner.next();
            System.out.print("Enter new password: ");
            var newPassword = scanner.next();
            return traineeService.changePassword(id, oldPassword, newPassword);
        } catch (CredentialException e) {
            logger.info("Password change failed: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.info("Error changing password: {}", e.getMessage());
            return null;
        }
    }

    public String deleteTraineeByUsername(Scanner scanner) {
        try {
            System.out.print("Enter user's username: ");
            var username = scanner.next();
            return traineeService.deleteByUsername(username);
        } catch (Exception e) {
            logger.error("Error deleting trainee by username: {}", e.getMessage());
            return "error";
        }
    }

    public TraineeDto activeAction(String username, Scanner scanner) {
        try {
            System.out.print("Enter active action(activate/deactivate): ");
            var activeAction = scanner.next();
            return activeAction.equals("activate") ? traineeService.activateAction(username)
                    : traineeService.deactivateAction(username);
        } catch (Exception e) {
            logger.info("Error active action: {}", e.getMessage());
            return null;
        }
    }
}
