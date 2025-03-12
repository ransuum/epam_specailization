package org.epam.util.render;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Trainer;
import org.epam.models.request.TrainingRequest;
import org.epam.util.servicecontroller.ServiceController;
import org.epam.util.subcontroller.SubControllerMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;
import java.util.Scanner;

import static org.epam.util.subcontroller.SubControllerMenu.existingUsernames;

@Component
public class MenuRenderer {
    private static final Log log = LogFactory.getLog(MenuRenderer.class);
    private final ServiceController serviceController;
    private SubControllerMenu subControllerMenu;

    public MenuRenderer(ServiceController serviceController) {
        this.serviceController = serviceController;
    }

    @Autowired
    public void setSubControllerMenu(SubControllerMenu subControllerMenu) {
        this.subControllerMenu = subControllerMenu;
    }

    public void renderAndProcessMenu() throws InterruptedException {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                displayMainMenu();
                byte input;
                try {
                    input = scanner.nextByte();
                } catch (InputMismatchException e) {
                    log.error("Invalid input! Please enter a number.", e);
                    scanner.nextLine();
                    continue;
                }

                running = processMenuOption(input, scanner);

                if (!running) {
                    log.info("Exiting application...");
                    Thread.sleep(2000);
                }
            }
        }
    }

    private void displayMainMenu() {
        String menu = """

                === Main Menu ===
                1. Create Trainee
                2. List Trainees
                3. Create Trainer
                4. List Trainers
                5. Create Training
                6. List Trainings
                7. Update Trainee
                8. Delete Trainee
                9. Find Trainee by ID
                10. Update Trainer
                11. Delete Trainer
                12. Find Trainer by ID
                13. Update Training
                14. Delete Training
                15. Find Training by ID
                0. Exit
                Choose an option:\s""";
        System.out.print(menu);
    }

    private boolean processMenuOption(byte input, Scanner scanner) {
        switch (input) {
            case 1:
                System.out.println(serviceController.getTraineeService().save(subControllerMenu.createTrainee(scanner)));
                break;
            case 2:
                serviceController.getTraineeService().findAll().forEach(System.out::println);
                break;
            case 3:
                System.out.println(serviceController.getTrainerService().save(subControllerMenu.createTrainer(scanner)));
                break;
            case 4:
                serviceController.getTrainerService().findAll().forEach(System.out::println);
                break;
            case 5:
                System.out.println(serviceController.getTrainingService().save(subControllerMenu.createTraining(scanner)));
                break;
            case 6:
                serviceController.getTrainingService().findAll().forEach(System.out::println);
                break;
            case 7:
                Pair<Integer, Trainee> pair = subControllerMenu.updateTrainee(scanner);
                System.out.println(serviceController.getTraineeService().update(pair.getLeft(), pair.getRight()));
                break;
            case 8:
                Integer id = subControllerMenu.deleteTrainee(scanner);
                existingUsernames.remove(serviceController.getTraineeService().findById(id).username());
                serviceController.getTraineeService().delete(id);
                break;
            case 9:
                System.out.println(serviceController.getTraineeService().findById(subControllerMenu.findTraineeById(scanner)));
                break;
            case 10:
                Pair<Integer, Trainer> trainerEpamPair = subControllerMenu.updateTrainer(scanner);
                if (trainerEpamPair != null) {
                    log.info("Updating trainer with ID: " + trainerEpamPair.getLeft());
                    System.out.println(serviceController.getTrainerService().update(trainerEpamPair.getLeft(), trainerEpamPair.getRight()));
                } else {
                    log.error("Trainer update failed due to invalid input.");
                }
                break;
            case 11:
                Integer trainerId = subControllerMenu.deleteTrainer(scanner);
                existingUsernames.remove(serviceController.getTrainerService().findById(trainerId).username());
                serviceController.getTrainerService().delete(trainerId);
                break;
            case 12:
                System.out.println(serviceController.getTrainerService().findById(subControllerMenu.findTrainerById(scanner)));
                break;
            case 13:
                Pair<Integer, TrainingRequest> trainingRequestEpamPair = subControllerMenu.updateTraining(scanner);
                System.out.println(serviceController.getTrainingService().update(trainingRequestEpamPair.getLeft(), trainingRequestEpamPair.getRight()));
                break;
            case 14:
                serviceController.getTrainingService().delete(subControllerMenu.deleteTraining(scanner));
                break;
            case 15:
                System.out.println(serviceController.getTrainingService().findById(subControllerMenu.findTrainingById(scanner)));
                break;
            case 0:
                return false;
            default:
                log.info("Invalid option, please try again.");
        }
        return true;
    }
}
