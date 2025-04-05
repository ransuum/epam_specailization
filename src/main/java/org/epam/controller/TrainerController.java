package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TrainerDto;
import org.epam.models.enums.UserType;
import org.epam.models.dto.create.TrainerCreateDto;
import org.epam.models.dto.update.TrainerUpdateDto;
import org.epam.service.TrainerService;
import org.epam.security.RequiredRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
@Tag(name = "Trainer Management", description = "APIs for managing trainer operations")
public class TrainerController {
    private final TrainerService trainerService;
    private final SecurityContextHolder securityContextHolder;

    @PostMapping("/register")
    @RequiredRole(UserType.NOT_AUTHORIZE)
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid TrainerCreateDto trainerCreateDto) {
        return new ResponseEntity<>(trainerService.save(trainerCreateDto), HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<TrainerDto> profile() {
        return new ResponseEntity<>(trainerService.findById(securityContextHolder.getUserId()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        trainerService.delete(id);
        return ResponseEntity.ok("DELETED");
    }

    @PutMapping("/update")
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<TrainerDto> updateTrainer(@RequestBody @Valid TrainerUpdateDto requestUpdate) {
        return ResponseEntity.ok(trainerService.update(securityContextHolder.getUserId(), requestUpdate));
    }

    @GetMapping("/all")
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<List<TrainerDto>> findAll() {
        return ResponseEntity.ok(trainerService.findAll());
    }

    @PutMapping("/change-password")
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<TrainerDto> changePassword(@RequestParam String oldPassword,
                                                     @RequestParam String newPassword) {
        return ResponseEntity.ok(trainerService.changePassword(securityContextHolder.getUserId(), oldPassword, newPassword));
    }

    @GetMapping("/username/{username}")
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<TrainerDto> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(trainerService.findByUsername(username));
    }

    @PatchMapping("/change-status/{trainerUsername}")
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<String> changeStatus(@PathVariable String trainerUsername) {
        trainerService.changeStatus(trainerUsername);
        return ResponseEntity.ok("Status changed");
    }

    @GetMapping("/unassigned/{traineeUsername}")
    @RequiredRole(UserType.TRAINER)
    public List<TrainerDto> getUnassignedTrainers(@PathVariable String traineeUsername) {
        return trainerService.getUnassignedTrainers(traineeUsername);
    }
}
