package org.epam.utils.menurender.choosermanagement;

import org.epam.controller.AuthenticationController;
import org.epam.controller.TraineeController;
import org.epam.controller.TrainerController;
import org.epam.controller.UserController;
import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.UserDto;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AuthenticationChooser implements Chooser {
    private final AuthenticationController authenticationController;
    private final UserController userController;
    private final TraineeController traineeController;
    private final TrainerController trainerController;
    private final TransactionExecution transExec;

    public AuthenticationChooser(AuthenticationController authenticationController, UserController userController,
                                 TraineeController traineeController, TrainerController trainerController, TransactionExecution transExec) {
        this.authenticationController = authenticationController;
        this.userController = userController;
        this.traineeController = traineeController;
        this.trainerController = trainerController;
        this.transExec = transExec;
    }

    @Override
    public void show(Scanner scanner, SecurityContextHolder securityContextHolder) {
        boolean traineeMenuRunning = true;

        while (traineeMenuRunning) {
            System.out.println("\n===== AUTHENTICATION MANAGEMENT =====");
            System.out.println("1. AUTHORIZE");
            System.out.println("2. LOGOUT");
            System.out.println("3. SIGN UP LIKE TRAINEE");
            System.out.println("4. SIGN UP LIKE TRAINER");
            System.out.println("0. Back to Main Menu");

            byte choice = getMenuChoice(scanner);
            scanner.nextLine();
            switch (choice) {
                case 1:
                    System.out.println(transExec.executeWithTransaction(()
                            -> authenticationController.authenticate(scanner)));
                    break;
                case 2:
                    System.out.println(transExec.executeWithTransaction(authenticationController::logout));
                    break;
                case 3:
                    System.out.println(transExec.executeWithTransaction(()
                            -> traineeController.addTrainee(scanner)));
                    break;
                case 4:
                    System.out.println(transExec.executeWithTransaction(()
                            -> trainerController.addTrainer(scanner)));
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
        return 6;
    }
}
