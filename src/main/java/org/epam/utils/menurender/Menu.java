package org.epam.utils.menurender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.exception.PermissionException;
import org.epam.models.SecurityContextHolder;
import org.epam.models.enums.UserType;
import org.epam.utils.menurender.choosermanagement.AuthenticationChooser;
import org.epam.utils.menurender.choosermanagement.Chooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private boolean running;
    private final Map<Integer, Chooser> chooserIntegerMap;
    private static final Logger log = LogManager.getLogger(Menu.class);
    private SecurityContextHolder securityContextHolder;

    public Menu(List<Chooser> choosers) {
        this.running = true;
        this.chooserIntegerMap = choosers.stream()
                .collect(Collectors.toMap(Chooser::getChoice, o -> o));
    }

    @Autowired
    public void setSecurityContextHolder(SecurityContextHolder securityContextHolder) {
        this.securityContextHolder = securityContextHolder;
    }

    public void show() {
        while (running) {
            displayMainMenu();
            int choice = getMenuChoice();
            processMainMenuChoice(choice);
        }
        scanner.close();
    }

    private void displayMainMenu() {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1. User Management");
        System.out.println("2. Trainee Management");
        System.out.println("3. Trainer Management");
        System.out.println("4. Training Management");
        System.out.println("5. Training View Management");
        System.out.println("6. Authorization Management");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private byte getMenuChoice() {
        return scanner.nextByte();
    }

    private void processMainMenuChoice(int choice) {
        scanner.nextLine();
        Boolean forTrainee = Objects.requireNonNull(securityContextHolder)
                .getUserType().equals(UserType.TRAINEE);
        Boolean forTrainer = Objects.requireNonNull(securityContextHolder)
                .getUserType().equals(UserType.TRAINER);
        switch (choice) {
            case 1:
                try {
                    if (checkForContext(forTrainee, forTrainer))
                        chooserIntegerMap.get(1).show(scanner, securityContextHolder);
                    else throw new PermissionException("Permission denied please authorize");
                } catch (Exception e) {
                    log.error("Security exception with user: {}", e.getMessage());
                }
                break;
            case 2:
                try {
                    if (Boolean.TRUE.equals(forTrainee))
                        chooserIntegerMap.get(2).show(scanner, securityContextHolder);
                    else throw new PermissionException("Permission denied");
                } catch (Exception e) {
                    log.error("Security exception with Trainee: {}", e.getMessage());
                }
                break;
            case 3:
                try {
                    if (Boolean.TRUE.equals(forTrainer))
                        chooserIntegerMap.get(3).show(scanner, securityContextHolder);
                    else throw new PermissionException("Permission denied");
                } catch (PermissionException e) {
                    log.error("Security exception with Trainer: {}", e.getMessage());
                }
                break;
            case 4:
                try {
                    if (checkForContext(forTrainee, forTrainer))
                        chooserIntegerMap.get(4).show(scanner, securityContextHolder);
                    else throw new PermissionException("Permission denied please authorize");
                } catch (PermissionException e) {
                    log.error("Security exception with training: {}", e.getMessage());
                }
                break;
            case 5:
                try {
                    if (checkForContext(forTrainee, forTrainer))
                        chooserIntegerMap.get(5).show(scanner, securityContextHolder);
                    else throw new PermissionException("Permission denied please authorize");
                } catch (PermissionException e) {
                    log.error("Security exception: {}", e.getMessage());
                }
                break;
            case 6:
                AuthenticationChooser authenticationChooser = (AuthenticationChooser) chooserIntegerMap.get(6);
                authenticationChooser.show(scanner, securityContextHolder);
                break;
            case 0:
                running = false;
                log.info("Exiting application. Goodbye!");
                break;
            default:
                log.error("Invalid choice. Please try again.");
                break;
        }
    }

    private boolean checkForContext(Boolean forTrainee, Boolean forTrainer) {
        return forTrainee || forTrainer;
    }
}
