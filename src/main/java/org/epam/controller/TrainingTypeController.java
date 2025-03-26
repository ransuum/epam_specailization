package org.epam.controller;

import org.epam.models.dto.TrainingTypeDto;
import org.epam.models.enums.TrainingName;
import org.epam.service.TrainingTypeService;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/training-type")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;
    private final TransactionExecution transactionExecution;

    public TrainingTypeController(TrainingTypeService trainingTypeService, TransactionExecution transactionExecution) {
        this.trainingTypeService = trainingTypeService;
        this.transactionExecution = transactionExecution;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingTypeDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(trainingTypeService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        transactionExecution.executeVoidWithTransaction(() -> trainingTypeService.delete(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TrainingTypeDto>> findAll() {
        return ResponseEntity.ok(trainingTypeService.findAll());
    }
}
