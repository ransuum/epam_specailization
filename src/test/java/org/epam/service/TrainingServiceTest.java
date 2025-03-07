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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepo trainingRepo;

    @Mock
    private TrainerRepo trainerRepo;

    @Mock
    private TraineeRepo traineeRepo;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private TrainingRequest request;

    @BeforeEach
    void setUp() {
        trainee = new Trainee("address", LocalDate.now(), "Valerii", "Dmitrenko", "valerii", "Password123@", Boolean.TRUE);
        trainer = new Trainer("Fitness", "Alex", "Johnson", "alex.j", "Fitness123@", Boolean.TRUE);
        training = new Training(trainee, trainer, "Java Basics", TrainingType.LABORATORY, LocalDate.now(), 2);
        request = new TrainingRequest(1, 1, 2, "Java Basics", TrainingType.LABORATORY, LocalDate.now(), 2);
    }

    @Test
    void save_ShouldCreateTraining() {
        when(traineeRepo.findById(1)).thenReturn(Optional.of(trainee));
        when(trainerRepo.findById(2)).thenReturn(Optional.of(trainer));
        when(trainingRepo.save(any(Training.class))).thenReturn(training);

        Training result = trainingService.save(request);

        assertNotNull(result);
        assertEquals("Java Basics", result.getTrainingName());
        verify(trainingRepo).save(any(Training.class));
    }

    @Test
    void save_ShouldThrowException_WhenTraineeNotFound() {
        when(traineeRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> trainingService.save(request));

        assertEquals("Trainee not found", exception.getMessage());
    }

    @Test
    void update_ShouldModifyExistingTraining() {
        when(trainingRepo.findById(1)).thenReturn(Optional.of(training));
        when(traineeRepo.findById(1)).thenReturn(Optional.of(trainee));
        when(trainerRepo.findById(2)).thenReturn(Optional.of(trainer));
        when(trainingRepo.update(any(Training.class))).thenReturn(training);

        Training updated = trainingService.update(request);

        assertNotNull(updated);
        assertEquals("Java Basics", updated.getTrainingName());
        verify(trainingRepo).update(any(Training.class));
    }

    @Test
    void update_ShouldThrowException_WhenTrainingNotFound() {
        when(trainingRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> trainingService.update(request));

        assertEquals("Training not found", exception.getMessage());
    }

    @Test
    void delete_ShouldRemoveTraining() {
        when(trainingRepo.findById(1)).thenReturn(Optional.of(training));

        trainingService.delete(1);

        verify(trainingRepo).delete(1);
    }

    @Test
    void findAll_ShouldReturnTrainings() {
        when(trainingRepo.findAll()).thenReturn(List.of(training));

        List<Training> trainings = trainingService.findAll();

        assertFalse(trainings.isEmpty());
        assertEquals(1, trainings.size());
    }

    @Test
    void findById_ShouldReturnTraining() {
        when(trainingRepo.findById(1)).thenReturn(Optional.of(training));

        Training result = trainingService.findById(1);

        assertNotNull(result);
        assertEquals("Java Basics", result.getTrainingName());
    }

    @Test
    void findById_ShouldThrowException_WhenTrainingNotFound() {
        when(trainingRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> trainingService.findById(1));

        assertEquals("Training not found", exception.getMessage());
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
                request.trainingName(),
                request.trainingType(),
                request.trainingDate(),
                request.trainingDuration());

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
}