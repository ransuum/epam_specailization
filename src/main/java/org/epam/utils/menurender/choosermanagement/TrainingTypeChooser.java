package org.epam.utils.menurender.choosermanagement;

import org.epam.controller.TrainingTypeController;
import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class TrainingTypeChooser implements Chooser {
    private final TrainingTypeController trainingTypeController;
    private final TransactionExecution transExec;

    public TrainingTypeChooser(TrainingTypeController trainingTypeController, TransactionExecution transExec) {
        this.trainingTypeController = trainingTypeController;
        this.transExec = transExec;
    }

    @Override
    public void show(Scanner scanner, SecurityContextHolder securityContextHolder) {
        boolean trainingViewMenuRunning = true;

        while (trainingViewMenuRunning) {
            System.out.println("\n===== TRAINING TYPE MANAGEMENT =====");
            System.out.println("1. Create Training Type");
            System.out.println("2. Find Training Type by ID");
            System.out.println("3. Update Training Type");
            System.out.println("4. Delete Training Type");
            System.out.println("5. List All Training Type");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            byte choice = getMenuChoice(scanner);
            scanner.nextLine();
            switch (choice) {
                case 1:
                    TrainingTypeDto newTrainingView = transExec.executeWithTransaction(()
                            -> trainingTypeController.create(scanner));
                    System.out.println("Training View created: " + newTrainingView);
                    break;
                case 2:
                    TrainingTypeDto trainingView = transExec.executeWithTransaction(()
                            -> trainingTypeController.findById(scanner));
                    System.out.println("Found training view: " + trainingView);
                    break;
                case 3:
                    TrainingTypeDto updatedTrainingView = transExec.executeWithTransaction(()
                            -> trainingTypeController.update(scanner));
                    System.out.println("Training View updated: " + updatedTrainingView);
                    break;
                case 4:
                    transExec.executeVoidWithTransaction(() -> trainingTypeController.delete(scanner));
                    System.out.println("Training View deleted successfully");
                    break;
                case 5:
                    transExec.executeVoidWithTransaction(trainingTypeController::findAll);
                    break;
                case 0:
                    trainingViewMenuRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    @Override
    public Integer getChoice() {
        return 5;
    }
}
