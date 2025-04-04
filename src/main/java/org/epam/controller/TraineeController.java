package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TraineeDto;
import org.epam.models.enums.UserType;
import org.epam.models.dto.create.TraineeCreateDto;
import org.epam.models.dto.update.TraineeRequestDto;
import org.epam.service.TraineeService;
import org.epam.transaction.transactionconfiguration.TransactionExecution;
import org.epam.security.RequiredRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainee")
@RequiredArgsConstructor
@Tag(name = "Trainee Management", description = "APIs for managing trainee operations")
public class TraineeController {
    private final TraineeService traineeService;
    private final SecurityContextHolder securityContextHolder;
    private final TransactionExecution transactionExecution;

    @PostMapping("/register")
    @RequiredRole(UserType.NOT_AUTHORIZE)
    public ResponseEntity<AuthResponseDto> register(@RequestBody TraineeCreateDto traineeCreateDto) {
        return new ResponseEntity<>(transactionExecution.executeWithTransaction(()
                -> traineeService.save(traineeCreateDto)), HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<TraineeDto> findById() {
        return new ResponseEntity<>(traineeService.findById(securityContextHolder.getUserId()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        transactionExecution.executeWithTransaction(() -> traineeService.delete(id));
        return ResponseEntity.ok("Deleted successfully!");
    }

    @PutMapping("/update")
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<TraineeDto> updateTrainee(@RequestBody @Valid TraineeRequestDto traineeRequestDto) {
        return ResponseEntity.ok(transactionExecution.executeWithTransaction(()
                -> traineeService.update(securityContextHolder.getUserId(), traineeRequestDto))
        );
    }

    @GetMapping
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<List<TraineeDto>> findAll() {
        return new ResponseEntity<>(traineeService.findAll(), HttpStatus.OK);
    }

    @PutMapping("/change-password")
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<String> changePassword(@RequestParam String oldPassword,
                                                 @RequestParam String newPassword) {
        transactionExecution.executeWithTransaction(()
                -> traineeService.changePassword(securityContextHolder.getUserId(), oldPassword, newPassword));
        return ResponseEntity.ok("Password changed");
    }

    @GetMapping("/username/{username}")
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<TraineeDto> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(traineeService.findByUsername(username));
    }

    @DeleteMapping("/username/{username}")
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<String> deleteTraineeByUsername(@PathVariable String username) {
        return ResponseEntity.ok(transactionExecution.executeWithTransaction(()
                -> traineeService.deleteByUsername(username)));
    }

    @PatchMapping("/change-status/{username}")
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<String> changeStatus(@PathVariable String username) {
        transactionExecution.executeWithTransaction(() -> traineeService.changeStatus(username));
        return ResponseEntity.ok("Changed status successfully");
    }
}
