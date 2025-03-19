package org.epam.controller;

import org.epam.models.dto.TraineeDto;
import org.epam.models.request.traineeRequest.TraineeRequestCreate;
import org.epam.models.request.traineeRequest.TraineeRequestUpdate;
import org.epam.service.TraineeService;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

@Controller
public class TraineeController {
    private final TraineeService traineeService;
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    public TraineeDto addTrainee(String userid, Scanner scanner) {
        System.out.print("Enter ur date of birth (dd-MM-yyyy): ");
        var dateOfBirth = scanner.next();
        System.out.print("Enter address: ");
        var address = scanner.next();
        return traineeService.save(new TraineeRequestCreate(userid, LocalDate.parse(dateOfBirth, formatter), address));
    }

    public TraineeDto findById(String id) {
        return traineeService.findById(id);
    }

    public void deleteById(Scanner scanner) {
        System.out.print("Enter id of trainee: ");
        String id = scanner.next();
        traineeService.delete(id);
    }

    public TraineeDto updateTrainee(String id, Scanner scanner) {
        System.out.print("Enter user's id: ");
        var userId = scanner.nextLine().trim();
        System.out.print("Enter address: ");
        var address = scanner.nextLine().trim();
        System.out.print("Enter ur date of birth (dd-MM-yyyy): ");
        var dateOfBirth = scanner.nextLine().trim();
        return traineeService.update(id, new TraineeRequestUpdate(userId, LocalDate.parse(dateOfBirth, formatter), address));
    }

    public void findAll() {
        traineeService.findAll().forEach(System.out::println);
    }

    public TraineeDto changePassword(String id, Scanner scanner) {
        System.out.print("Enter old password: ");
        var oldPassword = scanner.next();
        System.out.print("Enter new password: ");
        var newPassword = scanner.next();
        return traineeService.changePassword(id, oldPassword, newPassword);
    }

    public String deleteTraineeByUsername(Scanner scanner) {
        System.out.print("Enter user's username: ");
        var username = scanner.next();
        return traineeService.deleteByUsername(username);
    }

    public TraineeDto activeAction(String username, Scanner scanner) {
        System.out.print("Enter active action(activate/deactivate): ");
        var activeAction = scanner.next();
        return activeAction.equals("activate") ? traineeService.activateAction(username)
                : traineeService.deactivateAction(username);
    }
}
