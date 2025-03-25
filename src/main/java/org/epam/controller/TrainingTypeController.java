package org.epam.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.models.enums.TrainingName;
import org.epam.service.TrainingTypeService;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;
    private static final Logger logger = LogManager.getLogger(TrainingTypeController.class);

    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    public TrainingTypeDto create(Scanner scanner) {
        try {
            System.out.print("Enter training type: ");
            var trainingName = scanner.nextLine().trim();
            return trainingTypeService.save(TrainingName.getTrainingNameFromString(trainingName));
        } catch (Exception e) {
            logger.error("Error creating training view: {}", e.getMessage());
            return null;
        }
    }

    public TrainingTypeDto update(Scanner scanner) {
        try {
            System.out.print("Enter trainingView id: ");
            var trainingViewId = scanner.next();
            scanner.nextLine();
            System.out.print("Enter training type: ");
            var trainingName = scanner.nextLine().trim();
            return trainingTypeService.update(trainingViewId, TrainingName.getTrainingNameFromString(trainingName));
        } catch (Exception e) {
            logger.error("Error updating training view: {}", e.getMessage());
            return null;
        }
    }

    public TrainingTypeDto findById(Scanner scanner) {
        try {
            System.out.print("Enter trainingView id: ");
            var trainingViewId = scanner.next().trim();
            return trainingTypeService.findById(trainingViewId);
        } catch (Exception e) {
            logger.error("Error finding training view: {}", e.getMessage());
            return null;
        }
    }

    public void delete(Scanner scanner) {
        try {
            System.out.print("Enter trainingView id: ");
            var trainingViewId = scanner.next().trim();
            trainingTypeService.delete(trainingViewId);
        } catch (Exception e) {
            logger.error("Error deleting training view: {}", e.getMessage());
        }
    }

    public void findAll() {
        try {
            trainingTypeService.findAll().forEach(System.out::println);
        } catch (Exception e) {
            logger.error("Error find all training view: {}", e.getMessage());
        }
    }
}
