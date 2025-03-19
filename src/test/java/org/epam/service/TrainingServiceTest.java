package org.epam.service;

import org.epam.exception.NotFoundException;
import org.epam.models.entity.*;
import org.epam.models.enums.TrainingType;
import org.epam.models.request.trainingrequest.TrainingRequestCreate;
import org.epam.models.request.trainingrequest.TrainingRequestUpdate;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainerRepository;
import org.epam.repository.TrainingRepository;
import org.epam.repository.TrainingViewRepository;
import org.epam.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
    private static final String TEST_ID = "test-id";
    private static final String TRAINEE_ID = "trainee-id";
    private static final String TRAINER_ID = "trainer-id";
    private static final String TRAINING_VIEW_ID = "training-view-id";
    private static final String TRAINING_NAME = "Test Training";
    private static final LocalDate START_DATE = LocalDate.of(2025, 3, 15);
    private static final Long DURATION = 60L;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingViewRepository trainingViewRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainee testTrainee;
    private Trainer testTrainer;
    private TrainingView testTrainingView;
    private Training testTraining;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-id")
                .username("john.doe")
                .firstName("John")
                .lastName("Doe")
                .build();

        testTrainee = Trainee.builder()
                .id(TRAINEE_ID)
                .user(testUser)
                .build();

        testTrainer = Trainer.builder()
                .id(TRAINER_ID)
                .user(testUser)
                .specialization("Fitness")
                .build();

        testTrainingView = TrainingView.builder()
                .id(TRAINING_VIEW_ID)
                .trainingType(TrainingType.LABORATORY)
                .build();

        testTraining = Training.builder()
                .id(TEST_ID)
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingView(testTrainingView)
                .trainingName(TRAINING_NAME)
                .startTime(START_DATE)
                .duration(DURATION)
                .build();
    }

    @Test
    void save_shouldCreateNewTraining() {
        var request = new TrainingRequestCreate(
                TRAINEE_ID, TRAINER_ID, TRAINING_NAME, TRAINING_VIEW_ID, START_DATE, DURATION);

        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(testTrainer));
        when(trainingViewRepository.findById(TRAINING_VIEW_ID)).thenReturn(Optional.of(testTrainingView));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        var result = trainingService.save(request);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TRAINING_NAME, result.trainingName());
        assertEquals(START_DATE, result.startTime());
        assertEquals(DURATION, result.duration());

        verify(traineeRepository).findById(TRAINEE_ID);
        verify(trainerRepository).findById(TRAINER_ID);
        verify(trainingViewRepository).findById(TRAINING_VIEW_ID);
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void save_shouldReturnNullWhenTraineeNotFound() {
        var request = new TrainingRequestCreate(
                TRAINEE_ID, TRAINER_ID, TRAINING_NAME, TRAINING_VIEW_ID, START_DATE, DURATION);

        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(testTrainer));
        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.empty());

        var result = trainingService.save(request);

        assertNull(result);

        verify(trainerRepository).findById(TRAINER_ID);
        verify(traineeRepository).findById(TRAINEE_ID);
        verify(trainingViewRepository, never()).findById(any());
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void save_shouldReturnNullWhenTrainerNotFound() {
        var request = new TrainingRequestCreate(
                TRAINEE_ID, TRAINER_ID, TRAINING_NAME, TRAINING_VIEW_ID, START_DATE, DURATION);

        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.empty());

        var result = trainingService.save(request);

        assertNull(result);

        verify(trainerRepository).findById(TRAINER_ID);
        verify(traineeRepository, never()).findById(any());
        verify(trainingViewRepository, never()).findById(any());
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void save_shouldReturnNullWhenTrainingViewNotFound() {
        var request = new TrainingRequestCreate(
                TRAINEE_ID, TRAINER_ID, TRAINING_NAME, TRAINING_VIEW_ID, START_DATE, DURATION);

        when(traineeRepository.findById(TRAINEE_ID)).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findById(TRAINER_ID)).thenReturn(Optional.of(testTrainer));
        when(trainingViewRepository.findById(TRAINING_VIEW_ID)).thenReturn(Optional.empty());

        var result = trainingService.save(request);

        assertNull(result);
        verify(traineeRepository).findById(TRAINEE_ID);
        verify(trainerRepository).findById(TRAINER_ID);
        verify(trainingViewRepository).findById(TRAINING_VIEW_ID);
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateTrainingFields() {
        String newTrainingName = "Updated Training";
        Long newDuration = 90L;

        var request = new TrainingRequestUpdate(
                null, null, newTrainingName, null, null, newDuration);

        Training updatedTraining = Training.builder()
                .id(TEST_ID)
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingView(testTrainingView)
                .trainingName(newTrainingName)
                .startTime(START_DATE)
                .duration(newDuration)
                .build();

        when(trainingRepository.findById(TEST_ID)).thenReturn(Optional.of(testTraining));
        when(trainingRepository.save(any(Training.class))).thenReturn(updatedTraining);

        var result = trainingService.update(TEST_ID, request);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(newTrainingName, result.trainingName());
        assertEquals(newDuration, result.duration());

        verify(trainingRepository).findById(TEST_ID);
        verify(trainingRepository).save(any(Training.class));
        verify(traineeRepository, never()).findById(any());
        verify(trainerRepository, never()).findById(any());
        verify(trainingViewRepository, never()).findById(any());
    }

    @Test
    void update_shouldUpdateAllProvidedFields() {
        String newTraineeId = "new-trainee-id";
        String newTrainerId = "new-trainer-id";
        String newTrainingViewId = "new-training-view-id";
        String newTrainingName = "Updated Training";
        LocalDate newStartDate = LocalDate.of(2025, 4, 1);
        Long newDuration = 90L;

        var newTrainee = Trainee.builder().id(newTraineeId).build();
        var newTrainer = Trainer.builder().id(newTrainerId).build();
        var newTrainingView = TrainingView.builder().id(newTrainingViewId).build();

        var request = new TrainingRequestUpdate(
                newTraineeId, newTrainerId, newTrainingName, newTrainingViewId, newStartDate, newDuration);

        var updatedTraining = Training.builder()
                .id(TEST_ID)
                .trainee(newTrainee)
                .trainer(newTrainer)
                .trainingView(newTrainingView)
                .trainingName(newTrainingName)
                .startTime(newStartDate)
                .duration(newDuration)
                .build();

        when(trainingRepository.findById(TEST_ID)).thenReturn(Optional.of(testTraining));
        when(traineeRepository.findById(TEST_ID)).thenReturn(Optional.of(newTrainee));
        when(trainerRepository.findById(TEST_ID)).thenReturn(Optional.of(newTrainer));
        when(trainingViewRepository.findById(newTrainingViewId)).thenReturn(Optional.of(newTrainingView));
        when(trainingRepository.save(any(Training.class))).thenReturn(updatedTraining);

        var result = trainingService.update(TEST_ID, request);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(newTrainingName, result.trainingName());
        assertEquals(newDuration, result.duration());

        verify(trainingRepository).findById(TEST_ID);
        verify(traineeRepository).findById(TEST_ID);
        verify(trainerRepository).findById(TEST_ID);
        verify(trainingViewRepository).findById(newTrainingViewId);
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void update_shouldReturnNullWhenTrainingNotFound() {
        var request = new TrainingRequestUpdate(
                null, null, "Updated Training", null, null, 90L);

        when(trainingRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        var result = trainingService.update(TEST_ID, request);

        assertNull(result);
        verify(trainingRepository).findById(TEST_ID);
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteTraining() {
        doNothing().when(trainingRepository).delete(TEST_ID);

        trainingService.delete(TEST_ID);

        verify(trainingRepository).delete(TEST_ID);
    }

    @Test
    void delete_shouldHandleNotFoundExceptionGracefully() {
        doThrow(new NotFoundException("Training not found")).when(trainingRepository).delete(TEST_ID);

        assertDoesNotThrow(() -> trainingService.delete(TEST_ID));
        verify(trainingRepository).delete(TEST_ID);
    }

    @Test
    void findAll_shouldReturnAllTrainings() {
        var secondTraining = Training.builder()
                .id("second-id")
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingView(testTrainingView)
                .trainingName("Second Training")
                .build();

        when(trainingRepository.findAll()).thenReturn(Arrays.asList(testTraining, secondTraining));

        var result = trainingService.findAll();

        assertEquals(2, result.size());
        assertEquals(TEST_ID, result.get(0).id());
        assertEquals("second-id", result.get(1).id());
        assertEquals(TRAINING_NAME, result.get(0).trainingName());
        assertEquals("Second Training", result.get(1).trainingName());

        verify(trainingRepository).findAll();
    }

    @Test
    void findById_shouldReturnTrainingWhenFound() {
        when(trainingRepository.findById(TEST_ID)).thenReturn(Optional.of(testTraining));

        var result = trainingService.findById(TEST_ID);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TRAINING_NAME, result.trainingName());

        verify(trainingRepository).findById(TEST_ID);
    }

    @Test
    void findById_shouldReturnNullWhenNotFound() {
        when(trainingRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        var result = trainingService.findById(TEST_ID);

        assertNull(result);
        verify(trainingRepository).findById(TEST_ID);
    }

    @Test
    void findTrainingWithUsernameOfTrainee_shouldReturnFilteredTrainings() {
        String username = "john.doe";
        LocalDate fromDate = LocalDate.of(2025, 1, 1);
        LocalDate toDate = LocalDate.of(2025, 12, 31);
        String trainerName = "Jane Smith";
        TrainingType trainingType = TrainingType.LABORATORY;

        var trainings = Collections.singletonList(testTraining);

        when(trainingRepository.findTrainingWithUsernameOfTrainee(
                username, fromDate, toDate, trainerName, trainingType)).thenReturn(trainings);

        var result = trainingService.findTrainingWithUsernameOfTrainee(
                username, fromDate, toDate, trainerName, trainingType);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(trainingRepository).findTrainingWithUsernameOfTrainee(
                username, fromDate, toDate, trainerName, trainingType);
    }

    @Test
    void findTrainingWithUsernameOfTrainer_shouldReturnFilteredTrainings() {
        String username = "john.doe";
        LocalDate fromDate = LocalDate.of(2025, 1, 1);
        LocalDate toDate = LocalDate.of(2025, 12, 31);
        String traineeName = "Jane Smith";
        TrainingType trainingType = TrainingType.LABORATORY;

        var trainings = Collections.singletonList(testTraining);

        when(trainingRepository.findTrainingWithUsernameOfTrainer(
                username, fromDate, toDate, traineeName, trainingType)).thenReturn(trainings);

        var result = trainingService.findTrainingWithUsernameOfTrainer(
                username, fromDate, toDate, traineeName, trainingType);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(trainingRepository).findTrainingWithUsernameOfTrainer(
                username, fromDate, toDate, traineeName, trainingType);
    }
}