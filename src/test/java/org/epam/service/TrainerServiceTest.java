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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TrainerServiceTest {
    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private User testUsers;
    private Trainer testTrainer;
    private TrainingType testTrainingType;

    @BeforeEach
    void setUp() {
        testUsers = User.builder()
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
                .user(testUsers)
                .specialization(testTrainingType)
                .trainings(new ArrayList<>())
                .build();

        setupSecurityContext();
    }

    private void setupSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);
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
        var request = new TrainerUpdateDto(
                "Laboratory",
                testUsers.getFirstName(),
                testUsers.getLastName(),
                testUsers.getUsername(),
                testUsers.getIsActive()
        );

        var newSpecialization = TrainingType.builder()
                .id("newSpecializationId")
                .trainingTypeName(TrainingTypeName.LABORATORY)
                .build();

        var updatedTrainer = Trainer.builder()
                .id("trainerId")
                .user(testUsers)
                .specialization(newSpecialization)
                .trainings(new ArrayList<>())
                .build();

        when(trainerRepository.findByUser_Username(request.username()))
                .thenReturn(Optional.of(testTrainer));
        when(trainingTypeRepository.findByTrainingTypeName(TrainingTypeName.LABORATORY))
                .thenReturn(Optional.of(newSpecialization));
        when(trainerRepository.save(any(Trainer.class)))
                .thenReturn(updatedTrainer);

        var result = trainerService.update(request);

        assertNotNull(result);
        assertEquals("trainerId", result.id());
        assertEquals("Laboratory", result.specialization());

        // Verify interactions
        verify(trainerRepository).findByUser_Username(request.username());
        verify(trainingTypeRepository).findByTrainingTypeName(TrainingTypeName.LABORATORY);
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void changePassword_shouldUpdatePasswordSuccessfully() throws NotFoundException, CredentialException {
        String trainerId = "trainerId";
        String trainerUsername = "testUser";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        when(trainerRepository.findByUser_Username(trainerUsername)).thenReturn(Optional.of(testTrainer));

        var updatedTrainer = Trainer.builder()
                .id(trainerId)
                .user(User.builder()
                        .id("userId")
                        .username(trainerUsername)
                        .password(newPassword)
                        .isActive(true)
                        .build())
                .specialization(testTrainingType)
                .trainings(new ArrayList<>())
                .build();

        when(trainerRepository.save(any(Trainer.class))).thenReturn(updatedTrainer);

        var result = trainerService.changePassword(oldPassword, newPassword);

        assertNotNull(result);

        verify(trainerRepository).findByUser_Username(trainerUsername);
        verify(trainerRepository).save(any(Trainer.class));

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).save(trainerCaptor.capture());
        assertEquals(newPassword, trainerCaptor.getValue().getUser().getPassword());
    }

    @Test
    void changePassword_shouldReturnNullWhenOldPasswordMismatch() {
        when(trainerRepository.findByUser_Username("testUser")).thenReturn(Optional.of(testTrainer));

        assertThrows(CredentialException.class, () ->
                trainerService.changePassword("wrongOldPassword", "newPassword")
        );

        verify(trainerRepository).findByUser_Username("testUser");
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

        when(trainerRepository.findByUser_Username("testUser")).thenReturn(Optional.of(inactiveTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(activatedTrainer);

        var result = trainerService.changeStatus("testUser");

        assertNotNull(result);
        assertTrue(result.user().isActive());

        verify(trainerRepository).findByUser_Username("testUser");
        verify(trainerRepository).save(any(Trainer.class));

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).save(trainerCaptor.capture());
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

        when(trainerRepository.findByUser_Username("testUser")).thenReturn(Optional.of(testTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(deactivatedTrainer);

        var result = trainerService.changeStatus("testUser");

        assertNotNull(result);
        assertFalse(result.user().isActive());

        verify(trainerRepository).findByUser_Username("testUser");
        verify(trainerRepository).save(any(Trainer.class));

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).save(trainerCaptor.capture());
        assertFalse(trainerCaptor.getValue().getUser().getIsActive());
    }

    @Test
    void delete_shouldDeleteTrainer() throws NotFoundException {
        when(trainerRepository.findById("trainerId")).thenReturn(Optional.of(testTrainer));
        doNothing().when(trainerRepository).delete(testTrainer);

        trainerService.delete("trainerId");

        verify(trainerRepository).delete(testTrainer);
    }

    @Test
    void findByUsername_shouldReturnTrainerWhenFound() throws NotFoundException {
        when(trainerRepository.findByUser_Username("testUser")).thenReturn(Optional.of(testTrainer));

        var result = trainerService.findByUsername("testUser");

        assertNotNull(result);
        assertEquals("trainerId", result.id());
        assertEquals("testUser", result.user().username());

        verify(trainerRepository).findByUser_Username("testUser");
    }

    @Test
    void findByUsername_shouldReturnNullWhenNotFound() {
        when(trainerRepository.findByUser_Username("nonExistentUser")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.findByUsername("nonExistentUser"));

        verify(trainerRepository).findByUser_Username("nonExistentUser");
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

        when(traineeRepository.findByUser_Username("testTrainee")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAll()).thenReturn(List.of(assignedTrainer, unassignedTrainer));

        var result = trainerService.getUnassignedTrainers("testTrainee");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("unassignedTrainerId", result.getFirst().id());

        verify(traineeRepository).findByUser_Username("testTrainee");
        verify(trainerRepository).findAll();
    }

}