package org.epam.controller;

import org.epam.models.dto.TrainerDto;
import org.epam.models.request.trainerrequest.TrainerRequestCreate;
import org.epam.models.request.trainerrequest.TrainerRequestUpdate;
import org.epam.service.TrainerService;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
public class TrainerController {
    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    public TrainerDto addTrainer(String userId, Scanner scanner) {
        System.out.print("Enter specialization: ");
        var specialization = scanner.next();
        return trainerService.save(new TrainerRequestCreate(userId, specialization));
    }

    public TrainerDto findById(String id) {
        return trainerService.findById(id);
    }

    public void deleteById(Scanner scanner) {
        System.out.print("Enter id of trainee: ");
        var id = scanner.next();
        trainerService.delete(id);
    }

    public TrainerDto updateTrainer(String id, Scanner scanner) {
        System.out.print("Enter user's id: ");
        var userId = scanner.nextLine().trim();
        System.out.print("Enter specialization: ");
        var specialization = scanner.nextLine().trim();
        return trainerService.update(id, new TrainerRequestUpdate(userId, specialization));
    }

    public void findAll() {
        trainerService.findAll().forEach(System.out::println);
    }

    public TrainerDto changePassword(String id, Scanner scanner) {
        System.out.print("Enter old password: ");
        var oldPassword = scanner.next();
        System.out.print("Enter new password: ");
        var newPassword = scanner.next();
        return trainerService.changePassword(id, oldPassword, newPassword);
    }

    public TrainerDto activeAction(String username, Scanner scanner) {
        System.out.print("Enter active action(activate/deactivate): ");
        var activeAction = scanner.next();
        return activeAction.equals("activate") ? trainerService.activateAction(username)
                : trainerService.deactivateAction(username);
    }
}
