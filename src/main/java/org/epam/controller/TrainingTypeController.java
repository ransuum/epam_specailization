package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.models.enums.UserType;
import org.epam.service.TrainingTypeService;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.epam.utils.permissionforroles.RequiredRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/training-type")
@Tag(name = "TrainingType Management", description = "APIs for managing trainingType operations")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;
    private final TransactionExecution transactionExecution;

    public TrainingTypeController(TrainingTypeService trainingTypeService, TransactionExecution transactionExecution) {
        this.trainingTypeService = trainingTypeService;
        this.transactionExecution = transactionExecution;
    }

    @GetMapping("/{id}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<TrainingTypeDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(trainingTypeService.findById(id));
    }

    @DeleteMapping("/{id}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<?> delete(@PathVariable String id) {
        transactionExecution.executeVoidWithTransaction(() -> trainingTypeService.delete(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<List<TrainingTypeDto>> findAll() {
        return ResponseEntity.ok(trainingTypeService.findAll());
    }
}
