package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingListDto;
import org.epam.models.enums.TrainingName;
import org.epam.models.enums.UserType;
import org.epam.models.request.create.TrainingRequestCreate;
import org.epam.models.request.update.TrainingRequestUpdate;
import org.epam.service.TrainingService;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.epam.utils.permissionforroles.RequiredRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/training")
@Tag(name = "Training Management", description = "APIs for managing training operations")
public class TrainingController {
    private final TrainingService trainingService;
    private final TransactionExecution transactionExecution;
    private final SecurityContextHolder securityContextHolder;

    private List<TrainingRequestCreate> setTrainingRequestCreateList(String userId) {
        return List.of(
                new TrainingRequestCreate(userId, "uuid3", "FIRST NAME", "type1", "26-03-2025", 50L),
                new TrainingRequestCreate(userId, "uuid4", "FIRST NAME", "type2", "26-03-2022", 75L)
        );
    }

    public TrainingController(TrainingService trainingService, TransactionExecution transactionExecution, SecurityContextHolder securityContextHolder) {
        this.trainingService = trainingService;
        this.transactionExecution = transactionExecution;
        this.securityContextHolder = securityContextHolder;
    }

    @PostMapping
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<?> create(@RequestBody TrainingRequestCreate trainingRequestCreate) {
        transactionExecution.executeWithTransaction(() -> trainingService.save(trainingRequestCreate));
        return ResponseEntity.ok("Training request created successfully");
    }

    @GetMapping("/all")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<List<TrainingListDto>> findAll() {
        return ResponseEntity.ok(trainingService.findAll());
    }

    @GetMapping("/{trainingId}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<TrainingDto> findById(@PathVariable String trainingId) {
        return ResponseEntity.ok(trainingService.findById(trainingId));
    }

    @PutMapping("/{trainingId}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<TrainingDto> update(@PathVariable String trainingId, @RequestBody TrainingRequestUpdate update) {
        return ResponseEntity.ok(transactionExecution.executeWithTransaction(()
                -> trainingService.update(trainingId, update)));
    }

    @DeleteMapping("/{trainingId}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<?> delete(@PathVariable String trainingId) {
        transactionExecution.executeVoidWithTransaction(() -> trainingService.delete(trainingId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/by-trainee/{username}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<List<TrainingListDto.TrainingListDtoForUser>> getTraineeTrainings(@PathVariable String username,
                                                                                                          @RequestParam(required = false) String fromDate,
                                                                                                          @RequestParam(required = false) String toDate,
                                                                                                          @RequestParam(required = false) String trainerName,
                                                                                                          @RequestParam(required = false) TrainingName trainingName) {
        return ResponseEntity.ok(trainingService.getTraineeTrainings(username, fromDate, toDate, trainerName, trainingName));
    }

    @GetMapping("/by-trainer/{username}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<List<TrainingListDto.TrainingListDtoForUser>> getTrainerTrainings(@PathVariable String username,
                                                                                                          @RequestParam(required = false) String fromDate,
                                                                                                          @RequestParam(required = false) String toDate,
                                                                                                          @RequestParam(required = false) String traineeName,
                                                                                                          @RequestParam(required = false) TrainingName trainingName) {
        return ResponseEntity.ok(trainingService.getTrainerTrainings(username, fromDate, toDate, traineeName, trainingName));
    }

    @PostMapping("/add-to-trainee")
    @RequiredRole(UserType.TRAINEE)
    public List<TrainingDto> addListToTrainee() {
        return transactionExecution.executeWithTransaction(()
                -> trainingService.addTrainingsToTrainee(securityContextHolder.getUserId(),
                setTrainingRequestCreateList(securityContextHolder.getUserId()))
        );
    }
}
