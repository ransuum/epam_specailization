package org.epam.utils.menurender.choosermanagement;

import org.epam.controller.TrainingViewController;
import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.TrainingViewDto;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class TrainingViewChooser implements Chooser {
    private final TrainingViewController trainingViewController;
    private final TransactionExecution transExec;

    public TrainingViewChooser(TrainingViewController trainingViewController, TransactionExecution transExec) {
        this.trainingViewController = trainingViewController;
        this.transExec = transExec;
    }

    @Override
    public void show(Scanner scanner, SecurityContextHolder securityContextHolder) {
        boolean trainingViewMenuRunning = true;

        while (trainingViewMenuRunning) {
            System.out.println("\n===== TRAINING VIEW MANAGEMENT =====");
            System.out.println("1. Create Training View");
            System.out.println("2. Find Training View by ID");
            System.out.println("3. Update Training View");
            System.out.println("4. Delete Training View");
            System.out.println("5. List All Training Views");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            byte choice = getMenuChoice(scanner);
            scanner.nextLine();
            switch (choice) {
                case 1:
                    TrainingViewDto newTrainingView = transExec.executeWithTransaction(()
                            -> trainingViewController.create(scanner));
                    System.out.println("Training View created: " + newTrainingView);
                    break;
                case 2:
                    TrainingViewDto trainingView = transExec.executeWithTransaction(()
                            -> trainingViewController.findById(scanner));
                    System.out.println("Found training view: " + trainingView);
                    break;
                case 3:
                    TrainingViewDto updatedTrainingView = transExec.executeWithTransaction(()
                            -> trainingViewController.update(scanner));
                    System.out.println("Training View updated: " + updatedTrainingView);
                    break;
                case 4:
                    transExec.executeVoidWithTransaction(() -> trainingViewController.delete(scanner));
                    System.out.println("Training View deleted successfully");
                    break;
                case 5:
                    transExec.executeVoidWithTransaction(trainingViewController::findAll);
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
