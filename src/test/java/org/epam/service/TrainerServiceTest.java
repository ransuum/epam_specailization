package org.epam.service;

import org.epam.models.entity.Trainer;
import org.epam.models.entity.User;
import org.epam.models.request.trainerrequest.TrainerRequestCreate;
import org.epam.models.request.trainerrequest.TrainerRequestUpdate;
import org.epam.repository.TrainerRepository;
import org.epam.repository.UserRepository;
import org.epam.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {
    private static final String TEST_ID = "test-id";
    private static final String TEST_USER_ID = "test-user-id";
    private static final String TEST_USERNAME = "john.doe";
    private static final String TEST_SPECIALIZATION = "Fitness";
    private static final String OLD_PASSWORD = "oldPassword";
    private static final String NEW_PASSWORD = "newPassword";

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private User testUser;
    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(TEST_USER_ID)
                .username(TEST_USERNAME)
                .firstName("John")
                .lastName("Doe")
                .password(OLD_PASSWORD)
                .isActive(true)
                .build();

        testTrainer = Trainer.builder()
                .id(TEST_ID)
                .user(testUser)
                .specialization(TEST_SPECIALIZATION)
                .trainings(new ArrayList<>())
                .build();
    }

    @Test
    void save_shouldCreateNewTrainer() {
        var request = new TrainerRequestCreate(TEST_USER_ID, TEST_SPECIALIZATION);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        var result = trainerService.save(request);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TEST_SPECIALIZATION, result.specialization());
        assertEquals(TEST_USERNAME, result.user().username());

        verify(userRepository).findById(TEST_USER_ID);
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void save_shouldReturnNullWhenUserNotFound() {
        var request = new TrainerRequestCreate(TEST_USER_ID, TEST_SPECIALIZATION);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        var result = trainerService.save(request);

        assertNull(result);
        verify(userRepository).findById(TEST_USER_ID);
        verify(trainerRepository, never()).save(any(Trainer.class));
    }

    @Test
    void update_shouldUpdateSpecialization() {
        String newSpecialization = "Yoga";
        var request = new TrainerRequestUpdate(newSpecialization, null);

        var updatedTrainer = Trainer.builder()
                .id(TEST_ID)
                .user(testUser)
                .specialization(newSpecialization)
                .trainings(new ArrayList<>())
                .build();

        when(trainerRepository.findById(TEST_ID)).thenReturn(Optional.of(testTrainer));
        when(trainerRepository.update(eq(TEST_ID), any(Trainer.class))).thenReturn(updatedTrainer);

        var result = trainerService.update(TEST_ID, request);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(newSpecialization, result.specialization());

        verify(trainerRepository).findById(TEST_ID);
        verify(trainerRepository).update(eq(TEST_ID), any(Trainer.class));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void update_shouldUpdateUserWhenUserIdProvided() {
        String newUserId = "new-user-id";
        var request = new TrainerRequestUpdate(null, newUserId);

        var newUser = User.builder()
                .id(newUserId)
                .username("jane.smith")
                .firstName("Jane")
                .lastName("Smith")
                .password("password")
                .isActive(true)
                .build();

        var updatedTrainer = Trainer.builder()
                .id(TEST_ID)
                .user(newUser)
                .specialization(TEST_SPECIALIZATION)
                .trainings(new ArrayList<>())
                .build();

        when(trainerRepository.findById(TEST_ID)).thenReturn(Optional.of(testTrainer));
        when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
        when(trainerRepository.update(eq(TEST_ID), any(Trainer.class))).thenReturn(updatedTrainer);

        var result = trainerService.update(TEST_ID, request);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals("jane.smith", result.user().username());

        verify(trainerRepository).findById(TEST_ID);
        verify(userRepository).findById(newUserId);
        verify(trainerRepository).update(eq(TEST_ID), any(Trainer.class));
    }

    @Test
    void changePassword_shouldUpdatePasswordSuccessfully() {
        when(trainerRepository.findById(TEST_ID)).thenReturn(Optional.of(testTrainer));
        when(userRepository.update(eq(TEST_USER_ID), any(User.class))).thenReturn(testUser);

        var result = trainerService.changePassword(TEST_ID, OLD_PASSWORD, NEW_PASSWORD);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());

        verify(trainerRepository).findById(TEST_ID);
        verify(userRepository).update(eq(TEST_USER_ID), any(User.class));
    }

    @Test
    void changePassword_shouldReturnNullWhenOldPasswordMismatch() {
        when(trainerRepository.findById(TEST_ID)).thenReturn(Optional.of(testTrainer));

        var result = trainerService.changePassword(TEST_ID, "wrongPassword", NEW_PASSWORD);

        assertNull(result);
        verify(trainerRepository).findById(TEST_ID);
        verify(userRepository, never()).update(any(), any());
    }

    @Test
    void findById_shouldReturnTrainerWhenFound() {
        when(trainerRepository.findById(TEST_ID)).thenReturn(Optional.of(testTrainer));

        var result = trainerService.findById(TEST_ID);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TEST_SPECIALIZATION, result.specialization());

        verify(trainerRepository).findById(TEST_ID);
    }

    @Test
    void findById_shouldReturnNullWhenNotFound() {
        when(trainerRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        var result = trainerService.findById(TEST_ID);

        assertNull(result);
        verify(trainerRepository).findById(TEST_ID);
    }

    @Test
    void findAll_shouldReturnAllTrainers() {
        var trainer2 = Trainer.builder()
                .id("id-2")
                .user(User.builder().build())
                .specialization("Nutrition")
                .build();

        when(trainerRepository.findAll()).thenReturn(List.of(testTrainer, trainer2));

        var result = trainerService.findAll();

        assertEquals(2, result.size());
        verify(trainerRepository).findAll();
    }

    @Test
    void activateAction_shouldActivateTrainer() {
        var inactiveUser = User.builder()
                .id(TEST_USER_ID)
                .username(TEST_USERNAME)
                .isActive(false)
                .build();

        var inactiveTrainer = Trainer.builder()
                .id(TEST_ID)
                .user(inactiveUser)
                .specialization(TEST_SPECIALIZATION)
                .build();

        User activatedUser = User.builder()
                .id(TEST_USER_ID)
                .username(TEST_USERNAME)
                .isActive(true)
                .build();

        when(trainerRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(inactiveTrainer));
        when(userRepository.update(eq(TEST_USER_ID), any(User.class))).thenReturn(activatedUser);

        var result = trainerService.activateAction(TEST_USERNAME);

        assertNotNull(result);
        assertTrue(result.user().isActive());

        verify(trainerRepository).findByUsername(TEST_USERNAME);
        verify(userRepository).update(eq(TEST_USER_ID), any(User.class));
    }

    @Test
    void deactivateAction_shouldDeactivateTrainer() {
        var activeUser = User.builder()
                .id(TEST_USER_ID)
                .username(TEST_USERNAME)
                .isActive(true)
                .build();

        var activeTrainer = Trainer.builder()
                .id(TEST_ID)
                .user(activeUser)
                .specialization(TEST_SPECIALIZATION)
                .build();

        var deactivatedUser = User.builder()
                .id(TEST_USER_ID)
                .username(TEST_USERNAME)
                .isActive(false)
                .build();

        when(trainerRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(activeTrainer));
        when(userRepository.update(eq(TEST_USER_ID), any(User.class))).thenReturn(deactivatedUser);

        var result = trainerService.deactivateAction(TEST_USERNAME);

        assertNotNull(result);
        assertFalse(result.user().isActive());

        verify(trainerRepository).findByUsername(TEST_USERNAME);
        verify(userRepository).update(eq(TEST_USER_ID), any(User.class));
    }

    @Test
    void delete_shouldDeleteTrainer() {
        doNothing().when(trainerRepository).delete(TEST_ID);

        trainerService.delete(TEST_ID);

        verify(trainerRepository).delete(TEST_ID);
    }

    @Test
    void findByUsername_shouldReturnTrainerWhenFound() {
        when(trainerRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testTrainer));

        var result = trainerService.findByUsername(TEST_USERNAME);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TEST_USERNAME, result.user().username());

        verify(trainerRepository).findByUsername(TEST_USERNAME);
    }

    @Test
    void findByUsername_shouldReturnNullWhenNotFound() {
        when(trainerRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        var result = trainerService.findByUsername(TEST_USERNAME);

        assertNull(result);
        verify(trainerRepository).findByUsername(TEST_USERNAME);
    }

}