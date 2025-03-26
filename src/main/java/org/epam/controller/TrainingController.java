package org.epam.controller;

import org.epam.models.SecurityContextHolder;
import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingDtoForTrainee;
import org.epam.models.dto.TrainingDtoForTrainer;
import org.epam.models.enums.TrainingName;
import org.epam.models.request.create.TrainingRequestCreate;
import org.epam.models.request.update.TrainingRequestUpdate;
import org.epam.service.TrainingService;
import org.epam.utils.menurender.transactionconfiguration.TransactionExecution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/training")
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
    public ResponseEntity<?> create(@RequestBody TrainingRequestCreate trainingRequestCreate) {
        transactionExecution.executeWithTransaction(()
                -> trainingService.save(trainingRequestCreate));
        return ResponseEntity.ok("Training request created successfully");
    }

    @GetMapping
    public ResponseEntity<List<TrainingDto>> findAll() {
        return ResponseEntity.ok(trainingService.findAll());
    }

    @GetMapping("/{trainingId}")
    public ResponseEntity<TrainingDto> findById(@PathVariable String trainingId) {
        return ResponseEntity.ok(trainingService.findById(trainingId));
    }

    @PutMapping("/{trainingId}")
    public ResponseEntity<TrainingDto> update(@PathVariable String trainingId, @RequestBody TrainingRequestUpdate update) {
        return ResponseEntity.ok(transactionExecution.executeWithTransaction(()
                -> trainingService.update(trainingId, update)));
    }

    @DeleteMapping("/{trainingId}")
    public ResponseEntity<?> delete(@PathVariable String trainingId) {
        transactionExecution.executeVoidWithTransaction(() -> trainingService.delete(trainingId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/training-with-username-trainee")
    public ResponseEntity<List<TrainingDtoForTrainee>> findTrainingWithUsernameOfTrainee(@RequestParam String username,
                                                                         @RequestParam(required = false) String fromDate,
                                                                         @RequestParam(required = false) String toDate,
                                                                         @RequestParam(required = false) String trainerName,
                                                                         @RequestParam(required = false) TrainingName trainingName) {
        return ResponseEntity.ok(trainingService.findTrainingWithUsernameOfTrainee(username, fromDate, toDate, trainerName, trainingName));
    }

    @GetMapping("/training-with-username-trainer")
    public ResponseEntity<List<TrainingDtoForTrainer>> findTrainingWithUsernameOfTrainer(@RequestParam String username,
                                                                         @RequestParam(required = false) String fromDate,
                                                                         @RequestParam(required = false) String toDate,
                                                                         @RequestParam(required = false) String traineeName,
                                                                         @RequestParam(required = false) TrainingName trainingName) {
        return ResponseEntity.ok(trainingService.findTrainingWithUsernameOfTrainer(username, fromDate, toDate, traineeName, trainingName));
    }

    @PostMapping("/add-list-to-trainee")
    public List<TrainingDto> addListToTrainee() {
        return transactionExecution.executeWithTransaction(()
                -> trainingService.addTrainingsToTrainee(securityContextHolder.getUserId(),
                setTrainingRequestCreateList(securityContextHolder.getUserId()))
        );
    }
}
