package org.epam.service;

import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainerDto;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.models.dto.UserDto;
import org.epam.models.entity.*;
import org.epam.models.enums.TrainingName;
import org.epam.models.request.create.TrainerRequestUpdate;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainerRepository;
import org.epam.repository.TrainingTypeRepository;
import org.epam.repository.UserRepository;
import org.epam.service.impl.TrainerServiceImpl;
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
    private UserRepository userRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private User testUser;
    private Trainer testTrainer;
    private TrainingType testTrainingType;
    private TrainerDto testTrainerDto;
    private UserDto testUserDto;
    private TrainingTypeDto testTrainingTypeDto;

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
                .trainingName(TrainingName.SELF_PLACING)
                .build();

        testTrainer = Trainer.builder()
                .id("trainerId")
                .user(testUser)
                .specialization(testTrainingType)
                .trainings(new ArrayList<>())
                .build();

        testUserDto = new UserDto(
                "userId",
                "John",
                "Doe",
                "testUser",
                true,
                "oldPassword"
        );
        testTrainingTypeDto = new TrainingTypeDto("specializationId", TrainingName.SELF_PLACING.getVal(), Collections.emptyList(), Collections.emptyList());
        testTrainerDto = new TrainerDto("trainerId", testUserDto, Collections.emptyList(), testTrainingTypeDto);
    }

    @Test
    void save_shouldCreateNewTrainer() throws NotFoundException {
        TrainerRequestUpdate request = new TrainerRequestUpdate("userId", "specializationId");

        when(userRepository.findById("userId")).thenReturn(Optional.of(testUser));
        when(trainingTypeRepository.findById("specializationId")).thenReturn(Optional.of(testTrainingType));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        var result = trainerService.save(request);

        assertNotNull(result);
        assertEquals("trainerId", result.id());
        assertEquals("userId", result.user().id());
        assertEquals("specializationId", result.specialization().id());

        verify(userRepository).findById("userId");
        verify(trainingTypeRepository).findById("specializationId");
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void save_shouldReturnNullWhenUserNotFound() {
        TrainerRequestUpdate request = new TrainerRequestUpdate("nonExistentUserId", "specializationId");

        when(userRepository.findById("nonExistentUserId")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.save(request));
        verify(userRepository).findById("nonExistentUserId");
        verify(trainerRepository, never()).save(any(Trainer.class));
    }

    @Test
    void update_shouldUpdateSpecialization() throws NotFoundException {
        var request = new org.epam.models.request.update.TrainerRequestUpdate("newSpecializationId", null);
        var newSpecialization = TrainingType.builder()
                .id("newSpecializationId")
                .trainingName(TrainingName.LABORATORY)
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
        assertEquals("newSpecializationId", result.specialization().id());

        verify(trainerRepository).findById("trainerId");
        verify(trainingTypeRepository).findById("newSpecializationId");
        verify(trainerRepository).update(eq("trainerId"), any(Trainer.class));
    }

    @Test
    void update_shouldUpdateUserWhenUserIdProvided() throws NotFoundException {
        var request = new org.epam.models.request.update.TrainerRequestUpdate(null, "newUserId");
        var newUser = User.builder()
                .id("newUserId")
                .username("newUser")
                .isActive(true)
                .build();

        var updatedTrainer = Trainer.builder()
                .id("trainerId")
                .user(newUser)
                .specialization(testTrainingType)
                .trainings(new ArrayList<>())
                .build();

        when(trainerRepository.findById("trainerId")).thenReturn(Optional.of(testTrainer));
        when(userRepository.findById("newUserId")).thenReturn(Optional.of(newUser));
        when(trainerRepository.update(eq("trainerId"), any(Trainer.class))).thenReturn(updatedTrainer);

        var result = trainerService.update("trainerId", request);

        assertNotNull(result);
        assertEquals("trainerId", result.id());
        assertEquals("newUserId", result.user().id());

        verify(trainerRepository).findById("trainerId");
        verify(userRepository).findById("newUserId");
        verify(trainerRepository).update(eq("trainerId"), any(Trainer.class));
    }

    @Test
    void changePassword_shouldUpdatePasswordSuccessfully() throws NotFoundException, CredentialException {
        when(trainerRepository.findById("trainerId")).thenReturn(Optional.of(testTrainer));
        when(userRepository.update(eq("userId"), any(User.class))).thenReturn(testUser);

        var result = trainerService.changePassword("trainerId", "oldPassword", "newPassword");

        assertNotNull(result);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(eq("userId"), userCaptor.capture());
        assertEquals("newPassword", userCaptor.getValue().getPassword());

        verify(trainerRepository).findById("trainerId");
    }

    @Test
    void changePassword_shouldReturnNullWhenOldPasswordMismatch() {
        when(trainerRepository.findById("trainerId")).thenReturn(Optional.of(testTrainer));

        assertThrows(CredentialException.class, () ->
                trainerService.changePassword("trainerId", "wrongOldPassword", "newPassword")
        );

        verify(trainerRepository).findById("trainerId");
        verify(userRepository, never()).update(anyString(), any(User.class));
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

        when(trainerRepository.findByUsername("testUser")).thenReturn(Optional.of(inactiveTrainer));
        when(userRepository.update(eq("userId"), any(User.class))).thenReturn(testUser);

        var result = trainerService.changeStatus("testUser");

        assertNotNull(result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(eq("userId"), userCaptor.capture());
        assertTrue(userCaptor.getValue().getIsActive());

        verify(trainerRepository).findByUsername("testUser");
    }

    @Test
    void deactivateAction_shouldDeactivateTrainer() throws NotFoundException {
        when(trainerRepository.findByUsername("testUser")).thenReturn(Optional.of(testTrainer));

        var deactivatedUser = User.builder()
                .id("userId")
                .username("testUser")
                .isActive(false)
                .build();

        when(userRepository.update(eq("userId"), any(User.class))).thenReturn(deactivatedUser);

        var result = trainerService.changeStatus("testUser");

        assertNotNull(result);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(eq("userId"), userCaptor.capture());
        assertFalse(userCaptor.getValue().getIsActive());

        verify(trainerRepository).findByUsername("testUser");
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
                .user(User.builder().id("assignedUserId").username("assignedTrainer").build())
                .specialization(testTrainingType)
                .trainings(new ArrayList<>())
                .build();

        var unassignedTrainer = Trainer.builder()
                .id("unassignedTrainerId")
                .user(User.builder().id("unassignedUserId").username("unassignedTrainer").build())
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

        var result = trainerService.getUnassignedTrainersForTrainee("testTrainee");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("unassignedTrainerId", result.get(0).id());

        verify(traineeRepository).findByUsername("testTrainee");
        verify(trainerRepository).findAll();
    }

}