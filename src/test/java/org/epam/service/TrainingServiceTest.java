package org.epam.service;

import org.epam.exception.NotFoundException;
import org.epam.models.entity.*;
import org.epam.models.enums.TrainingTypeName;
import org.epam.models.dto.create.TrainingCreateDto;
import org.epam.models.dto.update.TraineeTrainingUpdateDto;
import org.epam.models.dto.update.TrainerTrainingUpdateDto;
import org.epam.models.dto.update.TrainingUpdateDto;
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
    private String testId;

    @BeforeEach
    void setUp() {
        testId = "test-id";
        var testUsers = new User();
        testUsers.setId("users-id");
        testUsers.setFirstName("John");
        testUsers.setLastName("Doe");
        testUsers.setUsername("johndoe");

        var testTrainerUsers = new User();
        testTrainerUsers.setId("trainer-users-id");
        testTrainerUsers.setFirstName("Jane");
        testTrainerUsers.setLastName("Smith");
        testTrainerUsers.setUsername("janesmith");

        testTrainee = new Trainee();
        testTrainee.setId("trainee-id");
        testTrainee.setUser(testUsers);
        testTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testTrainee.setAddress("123 Main St");

        testTrainingType = new TrainingType();
        testTrainingType.setId("training-type-id");
        testTrainingType.setTrainingTypeName(TrainingTypeName.SELF_PLACING);

        testTrainer = new Trainer();
        testTrainer.setId("trainer-id");
        testTrainer.setUser(testTrainerUsers);
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
        var request = new TrainingCreateDto(
                "johndoe",
                "janesmith",
                "Test Training",
                "Self Placing",
                "03-11-2025",
                60L
        );

        when(traineeRepository.findByUser_Username("johndoe")).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findByUser_Username("janesmith")).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findByTrainingTypeName(TrainingTypeName
                .getTrainingNameFromString(request.trainingTypeName()))).thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        var result = trainingService.save(request);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals("Test Training", result.trainingName());
        assertEquals(60L, result.duration());

        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void save_shouldThrowExceptionWhenTraineeNotFound() {
        var request = new TrainingCreateDto(
                "johndoe",
                "janesmith",
                "Test Training",
                "SELF_PLACING",
                "03-11-2025",
                60L
        );

        when(trainerRepository.findByUser_Username("janesmith")).thenReturn(Optional.of(testTrainer));

        var exception = assertThrows(NotFoundException.class, () ->
            trainingService.save(request));

        assertEquals("Trainee Not Found", exception.getMessage());
        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void save_shouldThrowExceptionWhenTrainerNotFound() {
        var request = new TrainingCreateDto(
                "johndoe",
                "janesmith",
                "Test Training",
                "SELF_PLACING",
                "03-11-2025",
                60L
        );

        when(trainerRepository.findByUser_Username("janesmith")).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () ->
            trainingService.save(request));

        assertEquals("Trainer Not Found", exception.getMessage());
        verify(trainingRepository, never()).save(any(Training.class));
        verify(traineeRepository, never()).findByUser_Username(anyString());
    }

    @Test
    void update_shouldUpdateTrainingFields() throws NotFoundException {
        var request = new TrainingUpdateDto(
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
        var request = new TrainingUpdateDto(
                "johndoe",
                "janesmith",
                "Updated Training",
                "training-type-id",
                "26-03-2025",
                90L
        );

        when(trainingRepository.findById(testId)).thenReturn(Optional.of(testTraining));
        when(traineeRepository.findByUser_Username("johndoe")).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findByUser_Username("janesmith")).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById("training-type-id")).thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        var result = trainingService.update(testId, request);

        assertNotNull(result);

        verify(trainingRepository).save(testTraining);
        verify(traineeRepository).findByUser_Username("johndoe");
        verify(trainerRepository).findByUser_Username("janesmith");
        verify(trainingTypeRepository).findById("training-type-id");
    }

    @Test
    void update_shouldThrowExceptionWhenTrainingNotFound() {
        var request = new TrainingUpdateDto(
                null,
                null,
                "Updated Training",
                null,
                null,
                90L
        );

        when(trainingRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> trainingService.update("non-existent-id", request));

        assertEquals("Training not found", exception.getMessage());
        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    void delete_shouldDeleteTraining() throws NotFoundException {
        when(trainingRepository.findById(testId)).thenReturn(Optional.of(testTraining));
        doNothing().when(trainingRepository).delete(testTraining);

        trainingService.delete(testId);

        verify(trainingRepository).delete(testTraining);
    }

    @Test
    void delete_shouldHandleNotFoundExceptionGracefully() {
        String nonExistentId = "non-existent-id";
        when(trainingRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> trainingService.delete(nonExistentId));

        assertEquals("Training Not Found", exception.getMessage());
        verify(trainingRepository, never()).delete(any(Training.class));
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
    void findById_shouldThrowExceptionWhenNotFound() {
        String nonExistentId = "non-existent-id";
        when(trainingRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> trainingService.findById(nonExistentId));

        assertEquals("Trainee not found by id " + nonExistentId, exception.getMessage());
    }

    @Test
    void updateTrainingsOfTrainee_shouldCreateTrainingsForTrainee() throws NotFoundException {
        var request = List.of(
                new TraineeTrainingUpdateDto(
                        "janesmith",
                        "Updated Training",
                        "Self Placing",
                        "26-03-2025",
                        90L
                )
        );

        when(traineeRepository.findByUser_Username("johndoe")).thenReturn(Optional.of(testTrainee));
        when(trainerRepository.findByUser_Username("janesmith")).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findByTrainingTypeName(TrainingTypeName.SELF_PLACING))
                .thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        var result = trainingService.updateTrainingsOfTrainee("johndoe", request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testId, result.getFirst().id());

        verify(traineeRepository).findByUser_Username("johndoe");
        verify(trainerRepository).findByUser_Username("janesmith");
        verify(trainingTypeRepository).findByTrainingTypeName(TrainingTypeName.SELF_PLACING);
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void check_shouldHandleNullValuesCorrectly() {
        var request = List.of(
                new TrainerTrainingUpdateDto(
                        "johndoe",
                        "Updated Training",
                        "Self Placing",
                        "26-03-2025",
                        90L
                )
        );

        when(trainerRepository.findByUser_Username("janesmith")).thenReturn(Optional.of(testTrainer));
        when(traineeRepository.findByUser_Username("johndoe")).thenReturn(Optional.of(testTrainee));
        when(trainingTypeRepository.findByTrainingTypeName(TrainingTypeName.SELF_PLACING))
                .thenReturn(Optional.of(testTrainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        var result = trainingService.updateTrainingsOfTrainer("janesmith", request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testId, result.getFirst().id());

        verify(trainerRepository).findByUser_Username("janesmith");
        verify(traineeRepository).findByUser_Username("johndoe");
        verify(trainingTypeRepository).findByTrainingTypeName(TrainingTypeName.SELF_PLACING);
        verify(trainingRepository).save(any(Training.class));
    }
}