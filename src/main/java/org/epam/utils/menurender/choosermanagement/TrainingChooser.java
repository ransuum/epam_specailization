package org.epam.utils.menurender.choosermanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.controller.TrainingController;
import org.epam.exception.PermissionException;
import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.TrainingDto;
import org.epam.models.enums.UserType;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class TrainingChooser implements Chooser {
    private static final Logger log = LogManager.getLogger(TrainingChooser.class);
    private final TrainingController trainingController;
    private final TransactionExecution transExec;

    public TrainingChooser(TrainingController trainingController, TransactionExecution transExec) {
        this.trainingController = trainingController;
        this.transExec = transExec;
    }

    @Override
    public void show(Scanner scanner, SecurityContextHolder securityContextHolder) {
        boolean trainingMenuRunning = true;
        while (trainingMenuRunning) {
            System.out.println("\n===== TRAINING MANAGEMENT =====");
            System.out.println("1. Create Training");
            System.out.println("2. Find Training by ID");
            System.out.println("3. Update Training");
            System.out.println("4. Delete Training");
            System.out.println("5. List All Trainings");
            System.out.println("6. List All Trainings by criteria for Trainee");
            System.out.println("7. List All Trainings by criteria for Trainer");
            System.out.println("8. add list of Training to Trainee");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            byte choice = getMenuChoice(scanner);
            scanner.nextLine();
            switch (choice) {
                case 1:
                    var newTraining = transExec.executeWithTransaction(()
                            -> trainingController.create(scanner));
                    System.out.println("Training created: " + newTraining);
                    break;
                case 2:
                    var training = trainingController.findById(scanner);
                    System.out.println("Found training: " + training);
                    break;
                case 3:
                    var updatedTraining = transExec.executeWithTransaction(()
                            -> trainingController.update(scanner));
                    System.out.println("Training updated: " + updatedTraining);
                    break;
                case 4:
                    transExec.executeVoidWithTransaction(() -> trainingController.delete(scanner));
                    System.out.println("Training deleted successfully");
                    break;
                case 5:
                    trainingController.findAll();
                    break;
                case 6:
                    trainingController.findTrainingWithUsernameOfTrainee(scanner).forEach(System.out::println);
                    break;
                case 7:
                    trainingController.findTrainingWithUsernameOfTrainer(scanner).forEach(System.out::println);
                    break;
                case 8:
                    try {
                        if (securityContextHolder.getUserType().equals(UserType.TRAINEE))
                            transExec.executeWithTransaction(()
                                    -> trainingController.addListToTrainee(securityContextHolder.getUserId())).forEach(System.out::println);
                        else throw new PermissionException("Permission denied because u r not a trainee");
                    } catch (Exception e) {
                        log.error("Error while adding list to training: {}", e.getMessage());
                    }
                    break;
                case 0:
                    trainingMenuRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    @Override
    public Integer getChoice() {
        return 4;
    }
}
