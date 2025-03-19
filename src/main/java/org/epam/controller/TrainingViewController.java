package org.epam.controller;

import org.epam.models.dto.TrainingViewDto;
import org.epam.models.enums.TrainingType;
import org.epam.service.TrainingViewService;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
public class TrainingViewController {
    private final TrainingViewService trainingViewService;

    public TrainingViewController(TrainingViewService trainingViewService) {
        this.trainingViewService = trainingViewService;
    }

    public TrainingViewDto create(Scanner scanner) {
        System.out.print("Enter training type: ");
        var trainingType = scanner.next();
        return trainingViewService.save(TrainingType.valueOf(trainingType.toUpperCase()));
    }

    public TrainingViewDto update(Scanner scanner) {
        System.out.print("Enter trainingView id: ");
        var trainingViewId = scanner.next();
        scanner.nextLine();
        System.out.print("Enter training type: ");
        var trainingType = scanner.nextLine().trim();
        return trainingViewService.update(trainingViewId, TrainingType.valueOf(trainingType.toUpperCase()));
    }

    public TrainingViewDto findById(Scanner scanner) {
        System.out.print("Enter trainingView id: ");
        var trainingViewId = scanner.next().trim();
        return trainingViewService.findById(trainingViewId);
    }

    public void delete(Scanner scanner) {
        System.out.print("Enter trainingView id: ");
        var trainingViewId = scanner.next().trim();
        trainingViewService.delete(trainingViewId);
    }

    public void findAll() {
        trainingViewService.findAll().forEach(System.out::println);
    }
}
