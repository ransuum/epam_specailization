package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.epam.models.dto.TraineeDto;
import org.epam.models.dto.update.TraineeRequestDto;
import org.epam.service.TraineeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainee")
@RequiredArgsConstructor
@Tag(name = "Trainee Management", description = "APIs for managing trainee operations")
public class TraineeController {
    private final TraineeService traineeService;

    @GetMapping("/id/{id}")
    public ResponseEntity<TraineeDto> findById(@PathVariable String id) {
        return new ResponseEntity<>(traineeService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('SCOPE_ACC_TRAINEE')")
    public ResponseEntity<TraineeDto> profile() {
        return new ResponseEntity<>(traineeService.profile(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        traineeService.delete(id);
        return ResponseEntity.ok("Deleted successfully!");
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('SCOPE_ACC_TRAINEE')")
    public ResponseEntity<TraineeDto> updateTrainee(@RequestBody @Valid TraineeRequestDto traineeRequestDto) {
        return ResponseEntity.ok(traineeService.update(traineeRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<TraineeDto>> findAll() {
        return new ResponseEntity<>(traineeService.findAll(), HttpStatus.OK);
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasAuthority('SCOPE_ACC_TRAINEE')")
    public ResponseEntity<String> changePassword(@RequestParam String oldPassword,
                                                 @RequestParam String newPassword) {
        traineeService.changePassword(oldPassword, newPassword);
        return ResponseEntity.ok("Password changed");
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<TraineeDto> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(traineeService.findByUsername(username));
    }

    @DeleteMapping("/username/{username}")
    public ResponseEntity<String> deleteTraineeByUsername(@PathVariable String username) {
        return ResponseEntity.ok(traineeService.deleteByUsername(username));
    }

    @PatchMapping("/change-status")
    @PreAuthorize("hasAuthority('SCOPE_ACC_TRAINEE')")
    public ResponseEntity<String> changeStatus() {
        traineeService.changeStatus();
        return ResponseEntity.ok("Changed status successfully");
    }
}
