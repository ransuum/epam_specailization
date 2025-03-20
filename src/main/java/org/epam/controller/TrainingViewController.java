package org.epam.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.models.dto.TrainingViewDto;
import org.epam.models.enums.TrainingType;
import org.epam.service.TrainingViewService;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
public class TrainingViewController {
    private final TrainingViewService trainingViewService;
    private static final Logger logger = LogManager.getLogger(TrainingViewController.class);

    public TrainingViewController(TrainingViewService trainingViewService) {
        this.trainingViewService = trainingViewService;
    }

    public TrainingViewDto create(Scanner scanner) {
        try {
            System.out.print("Enter training type: ");
            var trainingType = scanner.next();
            return trainingViewService.save(TrainingType.valueOf(trainingType.toUpperCase()));
        } catch (Exception e) {
            logger.error("Error creating training view: {}", e.getMessage());
            return null;
        }
    }

    public TrainingViewDto update(Scanner scanner) {
        try {
            System.out.print("Enter trainingView id: ");
            var trainingViewId = scanner.next();
            scanner.nextLine();
            System.out.print("Enter training type: ");
            var trainingType = scanner.nextLine().trim();
            return trainingViewService.update(trainingViewId, TrainingType.valueOf(trainingType.toUpperCase()));
        } catch (Exception e) {
            logger.error("Error updating training view: {}", e.getMessage());
            return null;
        }
    }

    public TrainingViewDto findById(Scanner scanner) {
        try {
            System.out.print("Enter trainingView id: ");
            var trainingViewId = scanner.next().trim();
            return trainingViewService.findById(trainingViewId);
        } catch (Exception e) {
            logger.error("Error finding training view: {}", e.getMessage());
            return null;
        }
    }

    public void delete(Scanner scanner) {
        try {
            System.out.print("Enter trainingView id: ");
            var trainingViewId = scanner.next().trim();
            trainingViewService.delete(trainingViewId);
        } catch (Exception e) {
            logger.error("Error deleting training view: {}", e.getMessage());
        }
    }

    public void findAll() {
        try {
            trainingViewService.findAll().forEach(System.out::println);
        } catch (Exception e) {
            logger.error("Error find all training view: {}", e.getMessage());
        }
    }
}
