package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.epam.models.dto.TrainerDto;
import org.epam.models.dto.update.TrainerUpdateDto;
import org.epam.service.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainer")
@RequiredArgsConstructor
@Tag(name = "Trainer Management", description = "APIs for managing trainer operations")
public class TrainerController {
    private final TrainerService trainerService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('SCOPE_ACC_TRAINER')")
    public ResponseEntity<TrainerDto> profile() {
        return new ResponseEntity<>(trainerService.profile(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        trainerService.delete(id);
        return ResponseEntity.ok("DELETED");
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('SCOPE_ACC_TRAINER')")
    public ResponseEntity<TrainerDto> updateTrainer(@RequestBody @Valid TrainerUpdateDto requestUpdate) {
        return ResponseEntity.ok(trainerService.update(requestUpdate));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TrainerDto>> findAll() {
        return ResponseEntity.ok(trainerService.findAll());
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasAuthority('SCOPE_ACC_TRAINER')")
    public ResponseEntity<TrainerDto> changePassword(@RequestParam String oldPassword,
                                                     @RequestParam String newPassword) {
        return ResponseEntity.ok(trainerService.changePassword(oldPassword, newPassword));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<TrainerDto> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(trainerService.findByUsername(username));
    }

    @PatchMapping("/change-status/{trainerUsername}")
    @PreAuthorize("hasAuthority('SCOPE_ACC_TRAINER')")
    public ResponseEntity<String> changeStatus(@PathVariable String trainerUsername) {
        trainerService.changeStatus(trainerUsername);
        return ResponseEntity.ok("Status changed");
    }

    @GetMapping("/unassigned/{traineeUsername}")
    @PreAuthorize("hasAuthority('SCOPE_ACC_TRAINER')")
    public List<TrainerDto> getUnassignedTrainers(@PathVariable String traineeUsername) {
        return trainerService.getUnassignedTrainers(traineeUsername);
    }
}
