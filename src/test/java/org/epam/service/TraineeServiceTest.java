package org.epam.service;

import org.epam.models.entity.Trainee;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingType;
import org.epam.models.request.TrainingRequest;
import org.epam.repository.TraineeRepo;
import org.epam.repository.TrainerRepo;
import org.epam.repository.TrainingRepo;
import org.epam.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TraineeServiceTest {
    private TrainingRepo trainingRepo;
    private TrainerRepo trainerRepo;
    private TraineeRepo traineeRepo;
    private TrainingService trainingService;

    @BeforeEach
    public void setUp() {
        trainingRepo = Mockito.mock(TrainingRepo.class);
        trainerRepo = Mockito.mock(TrainerRepo.class);
        traineeRepo = Mockito.mock(TraineeRepo.class);
        trainingService = new TrainingServiceImpl(trainingRepo, trainerRepo, traineeRepo);
    }

    @Test
    public void testSaveTraining_success() {
        int traineeId = 1;
        int trainerId = 1;
        Trainee trainee = new Trainee("address", LocalDate.now(), "John", "Doe", "john.doe", "Password1@", Boolean.TRUE);
        Trainer trainer = new Trainer("Fitness", "Alex", "Johnson", "alex.j", "Fitness123@", Boolean.TRUE);
        TrainingRequest request = TrainingRequest.builder()
                .traineeId(traineeId)
                .trainerId(trainerId)
                .trainingName("Test Training")
                .trainingType(TrainingType.LABORATORY)
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();

        Training expectedTraining = new Training(trainee, trainer,
                request.getTrainingName(),
                request.getTrainingType(),
                request.getTrainingDate(),
                request.getTrainingDuration());

        when(traineeRepo.findById(traineeId)).thenReturn(Optional.of(trainee));
        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(trainingRepo.save(any(Training.class))).thenReturn(expectedTraining);

        Training result = trainingService.save(request);
        assertNotNull(result);
        assertEquals("Test Training", result.getTrainingName());
        verify(trainingRepo, times(1)).save(any(Training.class));
    }

    @Test
    public void testSaveTraining_TraineeNotFound() {
        int traineeId = 1;
        int trainerId = 2;
        TrainingRequest request = TrainingRequest.builder()
                .traineeId(traineeId)
                .trainerId(trainerId)
                .trainingName("Test Training")
                .trainingType(TrainingType.LABORATORY)
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();
        when(traineeRepo.findById(traineeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> trainingService.save(request));
        assertEquals("Trainee not found", exception.getMessage());
    }

    @Test
    public void testUpdateTraining_success() {
        int trainingId = 1;
        Trainee trainee = new Trainee("address", LocalDate.now(), "John", "Doe", "john.doe", "Password1@", Boolean.TRUE);
        Trainer trainer = new Trainer("Fitness", "Alex", "Johnson", "alex.j", "Fitness123@", Boolean.TRUE);
        Training existingTraining = new Training(trainee, trainer, "Old Training", TrainingType.LABORATORY, LocalDate.now(), 60);

        TrainingRequest updateRequest = TrainingRequest.builder()
                .id(trainingId)
                .trainingName("Updated Training")
                .build();

        when(trainingRepo.findById(trainingId)).thenReturn(Optional.of(existingTraining));
        when(trainingRepo.update(any(Training.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Training updatedTraining = trainingService.update(updateRequest);

        assertEquals("Updated Training", updatedTraining.getTrainingName());
        verify(trainingRepo, times(1)).update(any(Training.class));
    }

    @Test
    public void testDeleteTraining_success() {
        int trainingId = 1;
        Trainee trainee = new Trainee("address", LocalDate.now(), "John", "Doe", "john.doe", "Password1@", Boolean.TRUE);
        Trainer trainer = new Trainer("Fitness", "Alex", "Johnson", "alex.j", "Fitness123@", Boolean.TRUE);
        Training existingTraining = new Training(trainee, trainer, "Test Training", TrainingType.LABORATORY, LocalDate.now(), 60);

        when(trainingRepo.findById(trainingId)).thenReturn(Optional.of(existingTraining));
        doNothing().when(trainingRepo).delete(trainingId);

        trainingService.delete(trainingId);

        verify(trainingRepo, times(1)).delete(trainingId);
    }

    @Test
    public void testFindAllTrainings() {
        Trainee trainee = new Trainee("address", LocalDate.now(), "John", "Doe", "john.doe", "Password1@", Boolean.TRUE);
        Trainer trainer = new Trainer("Fitness", "Alex", "Johnson", "alex.j", "Fitness123@", Boolean.TRUE);
        Training training1 = new Training(trainee, trainer, "Training1", TrainingType.LABORATORY, LocalDate.now(), 60);
        Training training2 = new Training(trainee, trainer, "Training2", TrainingType.LABORATORY, LocalDate.now(), 45);
        List<Training> trainings = Arrays.asList(training1, training2);
        when(trainingRepo.findAll()).thenReturn(trainings);

        List<Training> result = trainingService.findAll();

        assertEquals(2, result.size());
        verify(trainingRepo, times(1)).findAll();
    }

    @Test
    public void testFindTrainingById_success() {
        int trainingId = 1;
        Trainee trainee = new Trainee("address", LocalDate.now(), "John", "Doe", "john.doe", "Password1@", Boolean.TRUE);
        Trainer trainer = new Trainer("Fitness", "Alex", "Johnson", "alex.j", "Fitness123@", Boolean.TRUE);
        Training training = new Training(trainee, trainer, "Test Training", TrainingType.LABORATORY, LocalDate.now(), 60);
        training.setId(trainingId);
        when(trainingRepo.findById(trainingId)).thenReturn(Optional.of(training));

        Training result = trainingService.findById(trainingId);

        assertNotNull(result);
        assertEquals(trainingId, result.getId());
        verify(trainingRepo, times(1)).findById(trainingId);
    }
}