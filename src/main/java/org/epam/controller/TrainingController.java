package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingListDto;
import org.epam.models.enums.TrainingTypeName;
import org.epam.models.enums.UserType;
import org.epam.models.request.create.TrainingRequestCreate;
import org.epam.models.request.update.TraineeTrainingRequestUpdate;
import org.epam.models.request.update.TrainerTrainingRequestUpdate;
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

    public TrainingController(TrainingService trainingService, TransactionExecution transactionExecution) {
        this.trainingService = trainingService;
        this.transactionExecution = transactionExecution;
    }

    @PostMapping("/create")
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

    @PutMapping("/update/{trainingId}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<TrainingDto> update(@PathVariable String trainingId, @RequestBody TrainingRequestUpdate update) {
        return ResponseEntity.ok(transactionExecution.executeWithTransaction(()
                -> trainingService.update(trainingId, update)));
    }

    @DeleteMapping("/{trainingId}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<?> delete(@PathVariable String trainingId) {
        transactionExecution.executeVoidWithTransaction(() -> trainingService.delete(trainingId));
        return ResponseEntity.ok("DELETED");
    }

    @GetMapping("/by-trainee/{username}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<List<TrainingListDto.TrainingListDtoForUser>> getTraineeTrainings(@PathVariable String username,
                                                                                            @RequestParam(required = false) String fromDate,
                                                                                            @RequestParam(required = false) String toDate,
                                                                                            @RequestParam(required = false) String trainerName,
                                                                                            @RequestParam(required = false) TrainingTypeName trainingTypeName) {
        return ResponseEntity.ok(trainingService.getTraineeTrainings(username, fromDate, toDate, trainerName, trainingTypeName));
    }

    @GetMapping("/by-trainer/{username}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<List<TrainingListDto.TrainingListDtoForUser>> getTrainerTrainings(@PathVariable String username,
                                                                                            @RequestParam(required = false) String fromDate,
                                                                                            @RequestParam(required = false) String toDate,
                                                                                            @RequestParam(required = false) String traineeName,
                                                                                            @RequestParam(required = false) TrainingTypeName trainingTypeName) {
        return ResponseEntity.ok(trainingService.getTrainerTrainings(username, fromDate, toDate, traineeName, trainingTypeName));
    }

    @PutMapping("/add-to-trainee/{traineeUsername}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<List<TrainingDto>> updateTrainingsOfTrainee(@PathVariable String traineeUsername, @RequestBody List<TraineeTrainingRequestUpdate> trainingCreationData) {
        return ResponseEntity.ok(transactionExecution.executeWithTransaction(()
                -> trainingService.updateTrainingsOfTrainee(traineeUsername, trainingCreationData)));
    }

    @PutMapping("/add-to-trainer/{trainerUsername}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<List<TrainingDto>> updateTrainingsOfTrainer(@PathVariable String trainerUsername, @RequestBody List<TrainerTrainingRequestUpdate> trainingCreationData) {
        return ResponseEntity.ok(transactionExecution.executeWithTransaction(()
                -> trainingService.updateTrainingsOfTrainer(trainerUsername, trainingCreationData)));
    }
}
