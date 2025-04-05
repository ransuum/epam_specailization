package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.service.TrainingTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/training-type")
@RequiredArgsConstructor
@Tag(name = "TrainingType Management", description = "APIs for managing trainingType operations")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;

    @GetMapping("/{id}")
    public ResponseEntity<TrainingTypeDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(trainingTypeService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        trainingTypeService.delete(id);
        return ResponseEntity.ok("DELETED");
    }

    @GetMapping("/all")
    public ResponseEntity<List<TrainingTypeDto>> findAll() {
        return ResponseEntity.ok(trainingTypeService.findAll());
    }
}
