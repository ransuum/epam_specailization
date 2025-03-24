package org.epam.utils.menurender.choosermanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.controller.TrainerController;
import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.TrainerDto;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class TrainerChooser implements Chooser {
    private static final Logger log = LogManager.getLogger(TrainerChooser.class);
    private final TrainerController trainerController;
    private final TransactionExecution transExec;

    public TrainerChooser(TrainerController trainerController, TransactionExecution transExec) {
        this.trainerController = trainerController;
        this.transExec = transExec;
    }

    @Override
    public void show(Scanner scanner, SecurityContextHolder securityContextHolder) {
        boolean trainerMenuRunning = true;

        while (trainerMenuRunning) {
            System.out.println("\n===== TRAINER MANAGEMENT =====");
            System.out.println("1. Find Trainer by ID");
            System.out.println("2. Update Trainer");
            System.out.println("3. Delete Trainer");
            System.out.println("4. List All Trainers");
            System.out.println("5. Change Password");
            System.out.println("6. change status Trainer");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            byte choice = getMenuChoice(scanner);
            scanner.nextLine();
            switch (choice) {
                case 1:
                    TrainerDto trainer = transExec.executeWithTransaction(()
                            -> trainerController.findById(securityContextHolder.getUserId()));
                    System.out.println("Found trainer: " + trainer);
                    break;
                case 2:
                    TrainerDto updatedTrainer = transExec.executeWithTransaction(()
                            -> trainerController.updateTrainer(securityContextHolder.getUserId(), scanner));
                    System.out.println("Trainer updated: " + updatedTrainer);
                    break;
                case 3:
                    transExec.executeVoidWithTransaction(() -> trainerController.deleteById(scanner));
                    System.out.println("Trainer deleted successfully");
                    break;
                case 4:
                    transExec.executeVoidWithTransaction(trainerController::findAll);
                    break;
                case 5:
                    TrainerDto changePassword = transExec.executeWithTransaction(()
                            -> trainerController.changePassword(securityContextHolder.getUserId(), scanner));
                    System.out.println("Password changed for trainer: " + changePassword);
                    break;
                case 6:
                    System.out.println(transExec.executeWithTransaction(()
                            -> trainerController.changeStatus(securityContextHolder.getUsername())));
                case 0:
                    trainerMenuRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    @Override
    public Integer getChoice() {
        return 3;
    }
}
