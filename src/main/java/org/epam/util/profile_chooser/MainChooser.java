package org.epam.util.profile_chooser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.enums.Profile;
import org.epam.service.TraineeService;
import org.epam.service.TrainerService;
import org.epam.service.TrainingService;
import org.epam.util.sub_controller.SubControllerMenu;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;
import java.util.Scanner;

@Component
public class MainChooser implements Chooser {
    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingService trainingService;
    private static final Log log = LogFactory.getLog(MainChooser.class);
    private SubControllerMenu subControllerMenu;
    private AnnotationConfigApplicationContext context;

    @Override
    public void initialize(AnnotationConfigApplicationContext context) throws InterruptedException {
        this.context = context;
        this.traineeService = context.getBean(TraineeService.class);
        this.trainerService = context.getBean(TrainerService.class);
        this.trainingService = context.getBean(TrainingService.class);
        this.subControllerMenu = context.getBean(SubControllerMenu.class);
        process();
    }

    @Override
    public void process() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
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
            byte input;
            try {
                input = scanner.nextByte();
            } catch (InputMismatchException e) {
                log.error("Invalid input! Please enter a number.", e);
                scanner.nextLine();
                continue;
            }

            switch (input) {
                case 1:
                    System.out.println(traineeService.save(subControllerMenu.createTrainee(scanner)));
                    break;
                case 2:
                    traineeService.findAll().forEach(System.out::println);
                    break;
                case 3:
                    System.out.println(trainerService.save(subControllerMenu.createTrainer(scanner)));
                    break;
                case 4:
                    trainerService.findAll().forEach(System.out::println);
                    break;
                case 5:
                    System.out.println(trainingService.save(subControllerMenu.createTraining(scanner)));
                    break;
                case 6:
                    trainingService.findAll().forEach(System.out::println);
                    break;
                case 7:
                    System.out.println(traineeService.update(subControllerMenu.updateTrainee(scanner)));
                    break;
                case 8:
                    traineeService.delete(subControllerMenu.deleteTrainee(scanner));
                    break;
                case 9:
                    System.out.println(traineeService.findById(subControllerMenu.findTraineeById(scanner)));
                    break;
                case 10:
                    System.out.println(trainerService.update(subControllerMenu.updateTrainer(scanner)));
                    break;
                case 11:
                    trainerService.delete(subControllerMenu.deleteTrainer(scanner));
                    break;
                case 12:
                    System.out.println(trainerService.findById(subControllerMenu.findTrainerById(scanner)));
                    break;
                case 13:
                    System.out.println(trainingService.update(subControllerMenu.updateTraining(scanner)));
                    break;
                case 14:
                    trainingService.delete(subControllerMenu.deleteTraining(scanner));
                    break;
                case 15:
                    System.out.println(trainingService.findById(subControllerMenu.findTrainingById(scanner)));
                    break;
                case 0:
                    running = false;
                    log.info("Exiting application...");
                    Thread.sleep(2000);
                    break;
                default:
                    log.info("Invalid option, please try again.");
            }
        }
        scanner.close();
        context.close();
    }

    @Override
    public Profile getProfile() {
        return Profile.MAIN;
    }
}
