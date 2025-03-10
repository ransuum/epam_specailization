package org.epam.util.profilechooser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Trainer;
import org.epam.models.enums.Profile;
import org.epam.models.request.TrainingRequest;
import org.epam.service.TraineeService;
import org.epam.service.TrainerService;
import org.epam.service.TrainingService;
import org.epam.util.subcontroller.SubControllerMenu;
import org.graalvm.collections.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;
import java.util.Scanner;

import static org.epam.util.subcontroller.SubControllerMenu.existingUsernames;

@Component
public class MainChooser implements Chooser {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private SubControllerMenu subControllerMenu;
    private AnnotationConfigApplicationContext context;

    @Value("${spring.active.profile}")
    private String profile;

    private static final Log log = LogFactory.getLog(MainChooser.class);

    public MainChooser(TraineeService traineeService, TrainerService trainerService,
                       TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @Autowired
    public void setSubControllerMenu(SubControllerMenu subControllerMenu) {
        this.subControllerMenu = subControllerMenu;
    }

    @Override
    public void initialize(AnnotationConfigApplicationContext context) throws InterruptedException {
        this.context = context;
        log.info("Initializing application with FacadeChooser");
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
                    Pair<Integer, Trainee> pair = subControllerMenu.updateTrainee(scanner);
                    System.out.println(traineeService.update(pair.getLeft(), pair.getRight()));
                    break;
                case 8:
                    Integer id = subControllerMenu.deleteTrainee(scanner);
                    existingUsernames.remove(traineeService.findById(id).username());
                    traineeService.delete(id);
                    break;
                case 9:
                    System.out.println(traineeService.findById(subControllerMenu.findTraineeById(scanner)));
                    break;
                case 10:
                    Pair<Integer, Trainer> trainerEpamPair = subControllerMenu.updateTrainer(scanner);
                    if (trainerEpamPair != null) {
                        log.info("Updating trainer with ID: " + trainerEpamPair.getLeft());
                        System.out.println(trainerService.update(trainerEpamPair.getLeft(), trainerEpamPair.getRight()));
                    } else {
                        log.error("Trainer update failed due to invalid input.");
                    }
                    break;
                case 11:
                    Integer trainerId = subControllerMenu.deleteTrainer(scanner);
                    existingUsernames.remove(traineeService.findById(trainerId).username());
                    trainerService.delete(trainerId);
                    break;
                case 12:
                    System.out.println(trainerService.findById(subControllerMenu.findTrainerById(scanner)));
                    break;
                case 13:
                    Pair<Integer, TrainingRequest> trainingRequestEpamPair = subControllerMenu.updateTraining(scanner);
                    System.out.println(trainingService.update(trainingRequestEpamPair.getLeft(), trainingRequestEpamPair.getRight()));
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
    public boolean getProfile() {
        return profile.equals(Profile.main.name());
    }
}
