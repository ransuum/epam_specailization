package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.service.TrainingTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/training-type")
@RequiredArgsConstructor
@Tag(name = "TrainingType Management", description = "APIs for managing trainingType operations")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_AUTHORIZED')")
    public ResponseEntity<TrainingTypeDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(trainingTypeService.findById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_FULL_ACCESS')")
    public ResponseEntity<String> delete(@PathVariable String id) {
        trainingTypeService.delete(id);
        return ResponseEntity.ok("DELETED");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_AUTHORIZED')")
    public ResponseEntity<List<TrainingTypeDto>> findAll() {
        return ResponseEntity.ok(trainingTypeService.findAll());
    }
}
