package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.epam.utils.permissionforroles.RequiredRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainer")
@Tag(name = "Trainer Management", description = "APIs for managing trainer operations")
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
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<AuthResponseDto> addTrainer(@RequestBody TrainerRegistrationRequest request) {
        var save = userService.save(new UserRequestCreate(request.firstname(), request.lastname(), Boolean.TRUE));
        return new ResponseEntity<>(transactionExecution.executeWithTransaction(()
                -> trainerService.save(new TrainerRequestCreate(
                save.id(), request.specializationId()))), HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<TrainerDto> findById() {
        return new ResponseEntity<>(trainerService.findById(securityContextHolder.getUserId()), HttpStatus.FOUND);
    }

    @DeleteMapping("/{id}")
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        transactionExecution.executeVoidWithTransaction(() -> trainerService.delete(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<TrainerDto> updateTrainer(@RequestBody TrainerRequestUpdate requestUpdate) {
        return ResponseEntity.ok(transactionExecution.executeWithTransaction(()
                -> trainerService.update(securityContextHolder.getUserId(), requestUpdate)));
    }

    @GetMapping
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<List<TrainerDto>> findAll() {
        return ResponseEntity.ok(trainerService.findAll());
    }

    @PutMapping("/change-password")
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<TrainerDto> changePassword(@RequestParam String oldPassword,
                                                     @RequestParam String newPassword) {
        return ResponseEntity.ok(transactionExecution.executeWithTransaction(()
                -> trainerService.changePassword(securityContextHolder.getUserId(), oldPassword, newPassword)));
    }

    @GetMapping("/user/{username}")
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<TrainerDto> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(trainerService.findByUsername(username));
    }

    @PatchMapping("/change-status/{username}")
    @RequiredRole(UserType.TRAINER)
    public ResponseEntity<?> changeStatus(@PathVariable String username) {
        transactionExecution.executeWithTransaction(() -> trainerService.changeStatus(username));
        return ResponseEntity.ok("Status changed");
    }

    @GetMapping("/unassigned-trainers/{username}")
    @RequiredRole(UserType.TRAINER)
    public List<TrainerDto> getUnassignedTrainersForTrainee(@PathVariable String username) {
        return trainerService.getUnassignedTrainersForTrainee(username);
    }
}
