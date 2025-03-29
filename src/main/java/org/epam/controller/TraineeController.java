package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.epam.exception.PermissionException;
import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TraineeDto;
import org.epam.models.enums.UserType;
import org.epam.models.request.TraineeRegistrationRequest;
import org.epam.models.request.create.TraineeRequestCreate;
import org.epam.models.request.create.UserRequestCreate;
import org.epam.models.request.update.TraineeRequestUpdate;
import org.epam.service.TraineeService;
import org.epam.service.UserService;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.epam.utils.permissionforroles.RequiredRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/trainee")
@Tag(name = "Trainee Management", description = "APIs for managing trainee operations")
public class TraineeController {
    private final TraineeService traineeService;
    private final UserService userService;
    private final SecurityContextHolder securityContextHolder;
    private final TransactionExecution transactionExecution;
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public TraineeController(TraineeService traineeService, SecurityContextHolder securityContextHolder,
                             UserService userService, TransactionExecution transactionExecution) {
        this.traineeService = traineeService;
        this.userService = userService;
        this.securityContextHolder = securityContextHolder;
        this.transactionExecution = transactionExecution;
    }

    @PostMapping("/create")
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<AuthResponseDto> addTrainee(@RequestBody TraineeRegistrationRequest request) {
        var save = userService.save(new UserRequestCreate(request.firstname(), request.lastname(), Boolean.TRUE));
        return new ResponseEntity<>(transactionExecution.executeWithTransaction(()
                -> traineeService.save(new TraineeRequestCreate(save.id(), LocalDate.parse(request.dateOfBirth(), formatter), request.address()))
        ), HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<TraineeDto> findById() {
        if (securityContextHolder.getUserType().equals(UserType.TRAINER))
            throw new PermissionException("You are not allowed to perform this operation");
        return new ResponseEntity<>(traineeService.findById(securityContextHolder.getUserId()), HttpStatus.FOUND);
    }

    @DeleteMapping("/{id}")
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        transactionExecution.executeVoidWithTransaction(() -> traineeService.delete(id));
        return ResponseEntity.ok("Deleted successfully!");
    }

    @PutMapping("/update")
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<TraineeDto> updateTrainee(@RequestBody TraineeRequestUpdate traineeRequestUpdate) {
        return ResponseEntity.ok(transactionExecution.executeWithTransaction(()
                -> traineeService.update(securityContextHolder.getUserId(), traineeRequestUpdate))
        );
    }

    @GetMapping
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<List<TraineeDto>> findAll() {
        return new ResponseEntity<>(traineeService.findAll(), HttpStatus.OK);
    }

    @PutMapping("/change-password")
    @RequiredRole(UserType.TRAINEE)
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword,
                                            @RequestParam String newPassword) {
        transactionExecution.executeWithTransaction(()
                -> traineeService.changePassword(securityContextHolder.getUserId(), oldPassword, newPassword));
        return new ResponseEntity<>(HttpStatus.OK);
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
    public ResponseEntity<?> changeStatus(@PathVariable String username) {
        transactionExecution.executeWithTransaction(()
                -> traineeService.changeStatus(username));
        return ResponseEntity.ok("Changed status successfully");
    }
}
