package org.epam.utils.menurender.choosermanagement;

import org.epam.controller.TraineeController;
import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.TraineeDto;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class TraineeChooser implements Chooser {
    private final TraineeController traineeController;
    private final TransactionExecution transExec;

    public TraineeChooser(TraineeController controller, TransactionExecution transExec) {
        this.traineeController = controller;
        this.transExec = transExec;
    }

    @Override
    public void show(Scanner scanner, SecurityContextHolder securityContextHolder) {
        boolean traineeMenuRunning = true;

        while (traineeMenuRunning) {
            System.out.println("\n===== TRAINEE MANAGEMENT =====");
            System.out.println("1. Find Trainee by ID");
            System.out.println("2. Update Trainee");
            System.out.println("3. Delete Trainee by id");
            System.out.println("4. Delete Trainee by username");
            System.out.println("5. List All Trainees");
            System.out.println("6. Change Password");
            System.out.println("7. deactivate/activate Trainee");
            System.out.println("8. add list of Training to Trainee");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            byte choice = getMenuChoice(scanner);
            scanner.nextLine();
            switch (choice) {
                case 1:
                    TraineeDto trainee = transExec.executeWithTransaction(()
                            -> traineeController.findById(securityContextHolder.getUserId()));
                    System.out.println("Found trainee: " + trainee);
                    break;
                case 2:
                    TraineeDto updatedTrainee = transExec.executeWithTransaction(()
                            -> traineeController.updateTrainee(securityContextHolder.getUserId(), scanner));
                    System.out.println("Trainee updated: " + updatedTrainee);
                    break;
                case 3:
                    transExec.executeVoidWithTransaction(()
                            -> traineeController.deleteById(scanner));
                    System.out.println("Trainee deleted successfully");
                    break;
                case 4:
                    System.out.println(transExec.executeWithTransaction(() -> traineeController.deleteTraineeByUsername(scanner)));
                    break;
                case 5:
                    transExec.executeVoidWithTransaction(traineeController::findAll);
                    break;
                case 6:
                    TraineeDto changePassword = transExec.executeWithTransaction(()
                            -> traineeController.changePassword(securityContextHolder.getUserId(), scanner));
                    System.out.println("Password changed for trainee: " + changePassword);
                    break;
                case 7:
                    System.out.println(transExec.executeWithTransaction(()
                            -> traineeController.activeAction(securityContextHolder.getUserId(), scanner)));
                case 8:
                    transExec.executeWithTransaction(()
                            -> traineeController.addListToTrainee(securityContextHolder.getUserId(), scanner)).forEach(System.out::println);
                    break;
                case 0:
                    traineeMenuRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    @Override
    public Integer getChoice() {
        return 2;
    }
}
