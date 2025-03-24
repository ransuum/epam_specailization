package org.epam.service;

import org.epam.exception.NotFoundException;
import org.epam.models.entity.*;
import org.epam.models.enums.TrainingName;
import org.epam.models.request.createrequest.TrainingRequestCreate;
import org.epam.models.request.updaterequest.TrainingRequestUpdate;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainerRepository;
import org.epam.repository.TrainingRepository;
import org.epam.repository.TrainingTypeRepository;
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
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainee testTrainee;
    private Trainer testTrainer;
    private TrainingType testTrainingType;
    private Training testTraining;
    private User testUser;
    private String testId;

    @BeforeEach
    void setUp() {
        testId = "test-id";
        testUser = new User();
        testUser.setId("user-id");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUsername("johndoe");

        testTrainee = new Trainee();
        testTrainee.setId("trainee-id");
        testTrainee.setUser(testUser);
        testTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testTrainee.setAddress("123 Main St");

        testTrainingType = new TrainingType();
        testTrainingType.setId("training-view-id");
        testTrainingType.setTrainingName(TrainingName.SELF_PLACING);

        testTrainer = new Trainer();
        testTrainer.setId("trainer-id");
        testTrainer.setUser(testUser);
        testTrainer.setSpecialization(testTrainingType);

        testTraining = Training.builder()
                .id(testId)
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingName("Test Training")
                .trainingType(testTrainingType)
                .startTime(LocalDate.now())
                .duration(60L)
                .build();
    }

    @Test
    void save_shouldCreateNewTraining() throws NotFoundException {
        var request = new TrainingRequestCreate(
                "trainee-id",
                "trainer-id",
                "Test Training",
                "training-view-id",
                LocalDate.now(),
                60L
        );

        when(traineeRepository.findById("trainee-id")).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findById("trainer-id")).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById("training-view-id")).thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        var result = trainingService.save(request);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals("Test Training", result.trainingName());
        assertEquals(60L, result.duration());

        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void save_shouldReturnNullWhenTraineeNotFound() {
        var request = new TrainingRequestCreate(
                "non-existent-trainee",
                "trainer-id",
                "Test Training",
                "training-view-id",
                LocalDate.now(),
                60L
        );
        when(trainerRepository.findById("trainer-id")).thenReturn(Optional.of(testTrainer));

        when(traineeRepository.findById("non-existent-trainee")).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> {
            trainingService.save(request);
        });

        assertEquals("Trainee not found", exception.getMessage());
        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void save_shouldReturnNullWhenTrainerNotFound() {
        var request = new TrainingRequestCreate(
                "trainee-id",
                "non-existent-trainer",
                "Test Training",
                "training-view-id",
                LocalDate.now(),
                60L
        );

        when(trainerRepository.findById("non-existent-trainer")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            trainingService.save(request);
        });

        assertEquals("Trainer not found", exception.getMessage());
        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void save_shouldReturnNullWhenTrainingViewNotFound() {
        var request = new TrainingRequestCreate(
                "trainee-id",
                "trainer-id",
                "Test Training",
                "non-existent-view",
                LocalDate.now(),
                60L
        );

        when(traineeRepository.findById("trainee-id")).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findById("trainer-id")).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById("non-existent-view")).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> {
            trainingService.save(request);
        });

        assertEquals("Training type not found", exception.getMessage());
        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void update_shouldUpdateTrainingFields() throws NotFoundException {
        var request = new TrainingRequestUpdate(
                null,
                null,
                "Updated Training",
                null,
                null,
                90L
        );

        var updatedTraining = Training.builder()
                .id(testId)
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingName("Updated Training")
                .trainingType(testTrainingType)
                .startTime(LocalDate.now())
                .duration(90L)
                .build();

        when(trainingRepository.findById(testId)).thenReturn(Optional.of(testTraining));
        when(trainingRepository.save(any(Training.class))).thenReturn(updatedTraining);

        var result = trainingService.update(testId, request);

        assertNotNull(result);
        assertEquals("Updated Training", result.trainingName());
        assertEquals(90L, result.duration());

        verify(trainingRepository).save(testTraining);
    }

    @Test
    void update_shouldUpdateAllProvidedFields() throws NotFoundException {
        var request = new TrainingRequestUpdate(
                "trainee-id",
                "trainer-id",
                "Updated Training",
                "training-view-id",
                LocalDate.now().plusDays(7),
                90L
        );

        when(trainingRepository.findById(testId)).thenReturn(Optional.of(testTraining));
        when(traineeRepository.findById("trainee-id")).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findById("trainer-id")).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById("training-view-id")).thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        var result = trainingService.update(testId, request);

        assertNotNull(result);

        verify(trainingRepository).save(testTraining);
        verify(traineeRepository).findById("trainee-id");
        verify(trainerRepository).findById("trainer-id");
        verify(trainingTypeRepository).findById("training-view-id");
    }

    @Test
    void update_shouldReturnNullWhenTrainingNotFound() {
        var request = new TrainingRequestUpdate(
                null,
                null,
                "Updated Training",
                null,
                null,
                90L
        );

        when(trainingRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> {
            trainingService.update("non-existent-id", request);
        });

        assertEquals("Training not found", exception.getMessage());
        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void delete_shouldDeleteTraining() throws NotFoundException {
        doNothing().when(trainingRepository).delete(testId);

        trainingService.delete(testId);

        verify(trainingRepository).delete(testId);
    }

    @Test
    void delete_shouldHandleNotFoundExceptionGracefully() {
        String nonExistentId = "non-existent-id";
        doThrow(new NotFoundException("Training not found")).when(trainingRepository).delete(nonExistentId);

        var exception = assertThrows(NotFoundException.class, () -> {
            trainingService.delete(nonExistentId);
        });

        assertEquals("Training not found", exception.getMessage());
    }

    @Test
    void findAll_shouldReturnAllTrainings() {
        var trainings = List.of(testTraining);
        when(trainingRepository.findAll()).thenReturn(trainings);

        var result = trainingService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testId, result.getFirst().id());

        verify(trainingRepository).findAll();
    }

    @Test
    void findById_shouldReturnTrainingWhenFound() throws NotFoundException {
        when(trainingRepository.findById(testId)).thenReturn(Optional.of(testTraining));

        var result = trainingService.findById(testId);

        assertNotNull(result);
        assertEquals(testId, result.id());

        verify(trainingRepository).findById(testId);
    }

    @Test
    void findById_shouldReturnNullWhenNotFound() {
        String nonExistentId = "non-existent-id";
        when(trainingRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> {
            trainingService.findById(nonExistentId);
        });

        assertEquals("Trainee not found by id " + nonExistentId, exception.getMessage());
    }

    @Test
    void findTrainingWithUsernameOfTrainee_shouldReturnFilteredTrainings() {
        String username = "johndoe";
        LocalDate fromDate = LocalDate.now().minusDays(30);
        LocalDate toDate = LocalDate.now();
        String trainerName = "Jane";
        TrainingName trainingName = TrainingName.SELF_PLACING;

        var trainings = List.of(testTraining);
        when(trainingRepository.findTrainingWithUsernameOfTrainee(
                username, fromDate, toDate, trainerName, trainingName))
                .thenReturn(trainings);

        var result = trainingService.findTrainingWithUsernameOfTrainee(
                username, fromDate, toDate, trainerName, trainingName);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(trainingRepository).findTrainingWithUsernameOfTrainee(
                username, fromDate, toDate, trainerName, trainingName);
    }

    @Test
    void findTrainingWithUsernameOfTrainer_shouldReturnFilteredTrainings() {
        String username = "johndoe";
        LocalDate fromDate = LocalDate.now().minusDays(30);
        LocalDate toDate = LocalDate.now();
        String traineeName = "Jane";
        var trainingName = TrainingName.SELF_PLACING;

        var trainings = List.of(testTraining);
        when(trainingRepository.findTrainingWithUsernameOfTrainer(
                username, fromDate, toDate, traineeName, trainingName))
                .thenReturn(trainings);

        var result = trainingService.findTrainingWithUsernameOfTrainer(
                username, fromDate, toDate, traineeName, trainingName);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(trainingRepository).findTrainingWithUsernameOfTrainer(
                username, fromDate, toDate, traineeName, trainingName);
    }

    @Test
    void check_shouldHandleNullValuesCorrectly() {
        var requestWithNulls = new TrainingRequestUpdate(
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(trainingRepository.findById(testId)).thenReturn(Optional.of(testTraining));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        assertDoesNotThrow(() -> trainingService.update(testId, requestWithNulls));

        verify(traineeRepository, never()).findById(any());
        verify(trainerRepository, never()).findById(any());
        verify(trainingTypeRepository, never()).findById(any());
    }
}