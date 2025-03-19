package org.epam.utils.menurender.choosermanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.controller.UserController;
import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.UserDto;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class UserChooser implements Chooser {
    private static final Logger log = LogManager.getLogger(UserChooser.class);
    private final UserController userController;
    private final TransactionExecution transExec;

    public UserChooser(UserController userController, TransactionExecution transExec) {
        this.userController = userController;
        this.transExec = transExec;
    }

    @Override
    public void show(Scanner scanner, SecurityContextHolder securityContextHolder) {
        boolean userMenuRunning = true;

        while (userMenuRunning) {
            System.out.println("\n===== USER MANAGEMENT =====");
            System.out.println("1. Create User");
            System.out.println("2. Find User by ID");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. List All Users");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            byte choice = getMenuChoice(scanner);
            scanner.nextLine();
            switch (choice) {
                case 1:
                    try {
                        UserDto newUser = transExec.executeWithTransaction(()
                                -> userController.createUser(scanner));
                        System.out.println("User created: " + newUser);
                    } catch (Exception e) {
                        System.out.println("Error creating user: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        UserDto user = transExec.executeWithTransaction(()
                                -> userController.getUser(securityContextHolder.getUserId()));
                        System.out.println("Found user: " + user);
                    } catch (Exception e) {
                        System.out.println("Error finding user: " + e.getMessage());
                    }
                    break;
                case 3:
                    try {
                        UserDto updatedUser = transExec.executeWithTransaction(()
                                -> userController.updateUser(securityContextHolder.getUserId(), scanner));
                        System.out.println("User updated: " + updatedUser);
                    } catch (Exception e) {
                        System.out.println("Error updating user: " + e.getMessage());
                    }
                    break;
                case 4:
                    try {
                        transExec.executeVoidWithTransaction(() -> userController.deleteUser(scanner));
                        System.out.println("User deleted successfully");
                    } catch (Exception e) {
                        System.out.println("Error deleting user: " + e.getMessage());
                    }
                    break;
                case 5:
                    try {
                        transExec.executeVoidWithTransaction(userController::findAll);
                    } catch (Exception e) {
                        System.out.println("Error finding users: " + e.getMessage());
                    }
                    break;
                case 0:
                    userMenuRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    @Override
    public Integer getChoice() {
        return 1;
    }
}
