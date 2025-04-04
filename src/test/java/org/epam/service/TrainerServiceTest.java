package org.epam.service;

import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.*;
import org.epam.models.enums.TrainingTypeName;
import org.epam.models.dto.create.TrainerCreateDto;
import org.epam.models.dto.update.TrainerUpdateDto;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainerRepository;
import org.epam.repository.TrainingTypeRepository;
import org.epam.service.impl.TrainerServiceImpl;
import org.epam.utils.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {
    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private User testUser;
    private Trainer testTrainer;
    private TrainingType testTrainingType;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("userId")
                .username("testUser")
                .password("oldPassword")
                .isActive(true)
                .build();

        testTrainingType = TrainingType.builder()
                .id("specializationId")
                .trainingTypeName(TrainingTypeName.SELF_PLACING)
                .build();

        testTrainer = Trainer.builder()
                .id("trainerId")
                .user(testUser)
                .specialization(testTrainingType)
                .trainings(new ArrayList<>())
                .build();
    }

    @Test
    void save_shouldCreateNewTrainer() throws NotFoundException {
        var request = new TrainerCreateDto(
                "John",
                "Doe",
                TrainingTypeName.SELF_PLACING.getVal()
        );

        String generatedUsername = "john.doe";
        String generatedPassword = "password123";

        when(credentialsGenerator.generateUsername("John", "Doe")).thenReturn(generatedUsername);
        when(credentialsGenerator.generatePassword(generatedUsername)).thenReturn(generatedPassword);

        when(trainingTypeRepository.findByTrainingTypeName(TrainingTypeName.SELF_PLACING))
                .thenReturn(Optional.of(testTrainingType));

        var newUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username(generatedUsername)
                .password(generatedPassword)
                .isActive(true)
                .build();

        var newTrainer = Trainer.builder()
                .user(newUser)
                .specialization(testTrainingType)
                .build();

        when(trainerRepository.save(any(Trainer.class))).thenReturn(newTrainer);

        var result = trainerService.save(request);

        assertNotNull(result);
        assertEquals(generatedUsername, result.username());
        assertEquals(generatedPassword, result.password());

        verify(trainingTypeRepository).findByTrainingTypeName(TrainingTypeName.SELF_PLACING);
        verify(trainerRepository).save(any(Trainer.class));
        verify(credentialsGenerator).generateUsername("John", "Doe");
        verify(credentialsGenerator).generatePassword(generatedUsername);
    }

    @Test
    void save_shouldReturnNullWhenTrainingTypeNotFound() {
        TrainerCreateDto request = new TrainerCreateDto(
                "Non",
                "Existent",
                "Self Placing"
        );

        TrainingTypeName trainingTypeName = TrainingTypeName.getTrainingNameFromString("Self Placing");
        when(trainingTypeRepository.findByTrainingTypeName(trainingTypeName))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.save(request));

        verify(trainingTypeRepository).findByTrainingTypeName(trainingTypeName);
        verify(trainerRepository, never()).save(any(Trainer.class));
    }

    @Test
    void update_shouldUpdateSpecialization() throws NotFoundException {
        var request = new TrainerUpdateDto("newSpecializationId", testUser.getFirstName(),
                testUser.getLastName(), testUser.getUsername(), testUser.getIsActive());
        var newSpecialization = TrainingType.builder()
                .id("newSpecializationId")
                .trainingTypeName(TrainingTypeName.LABORATORY)
                .build();

        var updatedTrainer = Trainer.builder()
                .id("trainerId")
                .user(testUser)
                .specialization(newSpecialization)
                .trainings(new ArrayList<>())
                .build();

        when(trainerRepository.findById("trainerId")).thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findById("newSpecializationId")).thenReturn(Optional.of(newSpecialization));
        when(trainerRepository.update(eq("trainerId"), any(Trainer.class))).thenReturn(updatedTrainer);

        var result = trainerService.update("trainerId", request);

        assertNotNull(result);
        assertEquals("trainerId", result.id());
        assertEquals(newSpecialization.getTrainingTypeName().getVal(), result.specialization());

        verify(trainerRepository).findById("trainerId");
        verify(trainingTypeRepository).findById("newSpecializationId");
        verify(trainerRepository).update(eq("trainerId"), any(Trainer.class));
    }

    @Test
    void changePassword_shouldUpdatePasswordSuccessfully() throws NotFoundException, CredentialException {
        String trainerId = "trainerId";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(testTrainer));

        var updatedTrainer = Trainer.builder()
                .id(trainerId)
                .user(User.builder()
                        .id("userId")
                        .username("testUser")
                        .password(newPassword)
                        .isActive(true)
                        .build())
                .specialization(testTrainingType)
                .trainings(new ArrayList<>())
                .build();

        when(trainerRepository.update(eq(trainerId), any(Trainer.class))).thenReturn(updatedTrainer);

        var result = trainerService.changePassword(trainerId, oldPassword, newPassword);

        assertNotNull(result);

        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository).update(eq(trainerId), any(Trainer.class));

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).update(eq(trainerId), trainerCaptor.capture());
        assertEquals(newPassword, trainerCaptor.getValue().getUser().getPassword());
    }

    @Test
    void changePassword_shouldReturnNullWhenOldPasswordMismatch() {
        when(trainerRepository.findById("trainerId")).thenReturn(Optional.of(testTrainer));

        assertThrows(CredentialException.class, () ->
                trainerService.changePassword("trainerId", "wrongOldPassword", "newPassword")
        );

        verify(trainerRepository).findById("trainerId");
    }

    @Test
    void findById_shouldReturnTrainerWhenFound() throws NotFoundException {
        when(trainerRepository.findById("trainerId")).thenReturn(Optional.of(testTrainer));

        var result = trainerService.findById("trainerId");

        assertNotNull(result);
        assertEquals("trainerId", result.id());
        assertEquals("userId", result.user().id());

        verify(trainerRepository).findById("trainerId");
    }

    @Test
    void findById_shouldReturnNullWhenNotFound() {
        when(trainerRepository.findById("nonExistentId")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.findById("nonExistentId"));

        verify(trainerRepository).findById("nonExistentId");
    }

    @Test
    void findAll_shouldReturnAllTrainers() {
        List<Trainer> trainerList = Collections.singletonList(testTrainer);
        when(trainerRepository.findAll()).thenReturn(trainerList);

        var result = trainerService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("trainerId", result.getFirst().id());

        verify(trainerRepository).findAll();
    }

    @Test
    void activateAction_shouldActivateTrainer() throws NotFoundException {
        var inactiveUser = User.builder()
                .id("userId")
                .username("testUser")
                .isActive(false)
                .build();

        var inactiveTrainer = Trainer.builder()
                .id("trainerId")
                .user(inactiveUser)
                .specialization(testTrainingType)
                .trainings(new ArrayList<>())
                .build();

        var activatedTrainer = Trainer.builder()
                .id("trainerId")
                .user(User.builder()
                        .id("userId")
                        .username("testUser")
                        .isActive(true)
                        .build())
                .specialization(testTrainingType)
                .trainings(new ArrayList<>())
                .build();

        when(trainerRepository.findByUsername("testUser")).thenReturn(Optional.of(inactiveTrainer));
        when(trainerRepository.update(eq("trainerId"), any(Trainer.class))).thenReturn(activatedTrainer);

        var result = trainerService.changeStatus("testUser");

        assertNotNull(result);
        assertTrue(result.user().isActive());

        verify(trainerRepository).findByUsername("testUser");
        verify(trainerRepository).update(eq("trainerId"), any(Trainer.class));

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).update(eq("trainerId"), trainerCaptor.capture());
        assertTrue(trainerCaptor.getValue().getUser().getIsActive());
    }

    @Test
    void deactivateAction_shouldDeactivateTrainer() throws NotFoundException {
        var deactivatedTrainer = Trainer.builder()
                .id("trainerId")
                .user(User.builder()
                        .id("userId")
                        .username("testUser")
                        .password("oldPassword")
                        .isActive(false)
                        .build())
                .specialization(testTrainingType)
                .trainings(new ArrayList<>())
                .build();

        when(trainerRepository.findByUsername("testUser")).thenReturn(Optional.of(testTrainer));
        when(trainerRepository.update(eq("trainerId"), any(Trainer.class))).thenReturn(deactivatedTrainer);

        var result = trainerService.changeStatus("testUser");

        assertNotNull(result);
        assertFalse(result.user().isActive());

        verify(trainerRepository).findByUsername("testUser");
        verify(trainerRepository).update(eq("trainerId"), any(Trainer.class));

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).update(eq("trainerId"), trainerCaptor.capture());
        assertFalse(trainerCaptor.getValue().getUser().getIsActive());
    }

    @Test
    void delete_shouldDeleteTrainer() throws NotFoundException {
        doNothing().when(trainerRepository).delete("trainerId");

        trainerService.delete("trainerId");

        verify(trainerRepository).delete("trainerId");
    }

    @Test
    void findByUsername_shouldReturnTrainerWhenFound() throws NotFoundException {
        when(trainerRepository.findByUsername("testUser")).thenReturn(Optional.of(testTrainer));

        var result = trainerService.findByUsername("testUser");

        assertNotNull(result);
        assertEquals("trainerId", result.id());
        assertEquals("testUser", result.user().username());

        verify(trainerRepository).findByUsername("testUser");
    }

    @Test
    void findByUsername_shouldReturnNullWhenNotFound() {
        when(trainerRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.findByUsername("nonExistentUser"));

        verify(trainerRepository).findByUsername("nonExistentUser");
    }

    @Test
    void getUnassignedTrainersForTrainee_shouldReturnUnassignedTrainers() throws NotFoundException {
        var trainee = Trainee.builder()
                .id("traineeId")
                .user(User.builder().id("assignedUserId").username("assignedTrainer").build())
                .trainings(new ArrayList<>())
                .build();

        var assignedTrainer = Trainer.builder()
                .id("assignedTrainerId")
                .user(User.builder()
                        .id("assignedUserId")
                        .username("assignedTrainer")
                        .isActive(true)
                        .build())
                .specialization(testTrainingType)
                .trainings(new ArrayList<>())
                .build();

        var unassignedTrainer = Trainer.builder()
                .id("unassignedTrainerId")
                .user(User.builder()
                        .id("unassignedUserId")
                        .username("unassignedTrainer")
                        .isActive(true)
                        .build())
                .specialization(testTrainingType)
                .trainings(new ArrayList<>())
                .build();

        var assignedTraining = Training.builder()
                .id("trainingId")
                .trainer(assignedTrainer)
                .trainee(trainee)
                .build();

        trainee.getTrainings().add(assignedTraining);

        when(traineeRepository.findByUsername("testTrainee")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAll()).thenReturn(List.of(assignedTrainer, unassignedTrainer));

        var result = trainerService.getUnassignedTrainers("testTrainee");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("unassignedTrainerId", result.getFirst().id());

        verify(traineeRepository).findByUsername("testTrainee");
        verify(trainerRepository).findAll();
    }

}