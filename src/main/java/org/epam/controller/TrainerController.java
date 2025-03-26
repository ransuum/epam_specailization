package org.epam.controller;

import org.epam.exception.PermissionException;
import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TrainerDto;
import org.epam.models.enums.UserType;
import org.epam.models.request.TrainerRegistrationRequest;
import org.epam.models.request.create.TrainerRequestCreate;
import org.epam.models.request.create.UserRequestCreate;
import org.epam.models.request.update.TrainerRequestUpdate;
import org.epam.service.TrainerService;
import org.epam.service.UserService;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainer")
public class TrainerController {
    private final TrainerService trainerService;
    private final UserService userService;
    private final TransactionExecution transactionExecution;
    private final SecurityContextHolder securityContextHolder;

    public TrainerController(TrainerService trainerService, UserService userService,
                             TransactionExecution transactionExecution, SecurityContextHolder securityContextHolder) {
        this.trainerService = trainerService;
        this.userService = userService;
        this.transactionExecution = transactionExecution;
        this.securityContextHolder = securityContextHolder;
    }

    @PostMapping("/create")
    public ResponseEntity<AuthResponseDto> addTrainer(@RequestBody TrainerRegistrationRequest request) {
        var save = userService.save(new UserRequestCreate(request.firstname(), request.lastname(), Boolean.TRUE));
        return new ResponseEntity<>(transactionExecution.executeWithTransaction(()
                -> trainerService.save(new TrainerRequestCreate(
                save.id(), request.specializationId()))), HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<TrainerDto> findById() {
        if (securityContextHolder.getUserType().equals(UserType.TRAINEE))
            throw new PermissionException("You are not allowed to perform this operation");
        return new ResponseEntity<>(trainerService.findById(securityContextHolder.getUserId()), HttpStatus.FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        if (securityContextHolder.getUserType().equals(UserType.TRAINEE))
            throw new PermissionException("You are not allowed to perform this operation");
        transactionExecution.executeVoidWithTransaction(() -> trainerService.delete(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<TrainerDto> updateTrainer(@RequestBody TrainerRequestUpdate requestUpdate) {
        if (securityContextHolder.getUserType().equals(UserType.TRAINEE))
            throw new PermissionException("You are not allowed to perform this operation");
        return ResponseEntity.ok(transactionExecution.executeWithTransaction(()
                -> trainerService.update(securityContextHolder.getUserId(), requestUpdate)));
    }

    @GetMapping
    public ResponseEntity<List<TrainerDto>> findAll() {
        if (securityContextHolder.getUserType().equals(UserType.TRAINEE))
            throw new PermissionException("You are not allowed to perform this operation");
        return ResponseEntity.ok(trainerService.findAll());
    }

    @PutMapping("/change-password")
    public ResponseEntity<TrainerDto> changePassword(@RequestParam String oldPassword,
                                                     @RequestParam String newPassword) {
        if (securityContextHolder.getUserType().equals(UserType.TRAINEE))
            throw new PermissionException("You are not allowed to perform this operation");
        return ResponseEntity.ok(transactionExecution.executeWithTransaction(()
                -> trainerService.changePassword(securityContextHolder.getUserId(), oldPassword, newPassword)));
    }

    @PatchMapping("/change-status/{username}")
    public ResponseEntity<?> changeStatus(@PathVariable String username) {
        if (securityContextHolder.getUserType().equals(UserType.TRAINEE))
            throw new PermissionException("You are not allowed to perform this operation");
        transactionExecution.executeWithTransaction(() -> trainerService.changeStatus(username));
        return ResponseEntity.ok("Status changed");
    }

    @GetMapping("/unassigned-trainers/{username}")
    public List<TrainerDto> getUnassignedTrainersForTrainee(@PathVariable String username) {
        if (securityContextHolder.getUserType().equals(UserType.TRAINEE))
            throw new PermissionException("You are not allowed to perform this operation");
        return trainerService.getUnassignedTrainersForTrainee(username);
    }
}
