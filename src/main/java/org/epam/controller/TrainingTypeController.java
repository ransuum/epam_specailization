package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.models.enums.UserType;
import org.epam.service.TrainingTypeService;
import org.epam.utils.transactionconfiguration.TransactionExecution;
import org.epam.utils.permissionforroles.RequiredRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/training-type")
@RequiredArgsConstructor
@Tag(name = "TrainingType Management", description = "APIs for managing trainingType operations")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;
    private final TransactionExecution transactionExecution;

    @GetMapping("/{id}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<TrainingTypeDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(trainingTypeService.findById(id));
    }

    @DeleteMapping("/{id}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<String> delete(@PathVariable String id) {
        transactionExecution.executeVoidWithTransaction(() -> trainingTypeService.delete(id));
        return ResponseEntity.ok("DELETED");
    }

    @GetMapping("/all")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<List<TrainingTypeDto>> findAll() {
        return ResponseEntity.ok(trainingTypeService.findAll());
    }
}
