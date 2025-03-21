package org.epam.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.models.dto.TrainerDto;
import org.epam.models.request.trainerrequest.TrainerRequestCreate;
import org.epam.models.request.trainerrequest.TrainerRequestUpdate;
import org.epam.service.TrainerService;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Scanner;

@Controller
public class TrainerController {
    private final TrainerService trainerService;
    private static final Logger logger = LogManager.getLogger(TrainerController.class);

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    public TrainerDto addTrainer(String userId, Scanner scanner) {
        try {
            System.out.print("Enter specialization: ");
            var specialization = scanner.next();
            return trainerService.save(new TrainerRequestCreate(userId, specialization));
        } catch (Exception e) {
            logger.error("Error adding trainer: {}", e.getMessage());
            return null;
        }
    }

    public TrainerDto findById(String id) {
        try {
            return trainerService.findById(id);
        } catch (Exception e) {
            logger.error("Error finding trainer by ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public void deleteById(Scanner scanner) {
        try {
            System.out.print("Enter id of trainee: ");
            var id = scanner.next();
            trainerService.delete(id);
        } catch (Exception e) {
            logger.error("Error deleting trainer: {}", e.getMessage());
        }
    }

    public TrainerDto updateTrainer(String id, Scanner scanner) {
        try {
            System.out.print("Enter user's id: ");
            var userId = scanner.nextLine().trim();
            System.out.print("Enter specialization: ");
            var specialization = scanner.nextLine().trim();
            return trainerService.update(id, new TrainerRequestUpdate(userId, specialization));
        } catch (Exception e) {
            logger.error("Error updating trainer: {}", e.getMessage());
            return null;
        }
    }

    public void findAll() {
        try {
            trainerService.findAll().forEach(System.out::println);
        } catch (Exception e) {
            logger.error("Error retrieving all trainers: {}", e.getMessage(), e);
        }
    }

    public TrainerDto changePassword(String id, Scanner scanner) {
        try {
            System.out.print("Enter old password: ");
            var oldPassword = scanner.next();
            System.out.print("Enter new password: ");
            var newPassword = scanner.next();
            return trainerService.changePassword(id, oldPassword, newPassword);
        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage(), e);
            return null;
        }
    }

    public TrainerDto activeAction(String username, Scanner scanner) {
        try {
            System.out.print("Enter active action(activate/deactivate): ");
            var activeAction = scanner.next();
            return activeAction.equals("activate") ? trainerService.activateAction(username)
                    : trainerService.deactivateAction(username);
        } catch (Exception e) {
            logger.error("Error updating activation status: {}", e.getMessage(), e);
            return null;
        }
    }

    public List<TrainerDto> getUnassignedTrainersForTrainee(Scanner scanner) {
        try {
            System.out.print("Enter trainee's username: ");
            var username = scanner.next();
            return trainerService.getUnassignedTrainersForTrainee(username);
        } catch (Exception e) {
            logger.error("Error get unassigned Trainers by trainee's username: {}", e.getMessage(), e);
            return null;
        }

    }
}
