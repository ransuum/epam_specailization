package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingListDto;
import org.epam.models.enums.TrainingTypeName;
import org.epam.models.enums.UserType;
import org.epam.models.dto.create.TrainingCreateDto;
import org.epam.models.dto.update.TraineeTrainingUpdateDto;
import org.epam.models.dto.update.TrainerTrainingUpdateDto;
import org.epam.service.TrainingService;
import org.epam.security.RequiredRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/training")
@Tag(name = "Training Management", description = "APIs for managing training operations")
@RequiredArgsConstructor
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping("/create")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<String> create(@RequestBody @Valid TrainingCreateDto trainingCreateDto) {
        trainingService.save(trainingCreateDto);
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
    public ResponseEntity<List<TrainingDto>> updateTrainingsOfTrainee(@PathVariable String traineeUsername,
                                                                      @RequestBody List<TraineeTrainingUpdateDto> trainingCreationData) {
        return ResponseEntity.ok(trainingService.updateTrainingsOfTrainee(traineeUsername, trainingCreationData));
    }

    @PutMapping("/add-to-trainer/{trainerUsername}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<List<TrainingDto>> updateTrainingsOfTrainer(@PathVariable String trainerUsername,
                                                                      @RequestBody List<TrainerTrainingUpdateDto> trainingCreationData) {
        return ResponseEntity.ok(trainingService.updateTrainingsOfTrainer(trainerUsername, trainingCreationData));
    }
}
