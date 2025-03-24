package org.epam.service;

import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.User;
import org.epam.models.request.createrequest.TraineeRequestCreate;
import org.epam.models.request.updaterequest.TraineeRequestUpdate;
import org.epam.repository.TraineeRepository;
import org.epam.repository.UserRepository;
import org.epam.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private User testUser;
    private Trainee testTrainee;
    private TraineeRequestCreate testTraineeRequest;
    private TraineeRequestUpdate testTraineeUpdateRequest;
    private final String testId = "test-id";
    private final String testUsername = "testuser";
    private final String testPassword = "password";
    private final String testNewPassword = "newpassword";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(testId);
        testUser.setUsername(testUsername);
        testUser.setPassword(testPassword);
        testUser.setIsActive(true);

        testTrainee = new Trainee();
        testTrainee.setId(testId);
        testTrainee.setUser(testUser);
        testTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testTrainee.setAddress("Test Address");

        testTraineeRequest = new TraineeRequestCreate(
                testId,
                LocalDate.of(1990, 1, 1),
                "Test Address"
        );

        testTraineeUpdateRequest = new TraineeRequestUpdate();
        testTraineeUpdateRequest.setUserId(testId);
        testTraineeUpdateRequest.setDateOfBirth(LocalDate.of(1991, 2, 2));
        testTraineeUpdateRequest.setAddress("Updated Address");
    }

    @Test
    void save_shouldCreateNewTrainee() {
        when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        var result = traineeService.save(testTraineeRequest);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(userRepository).findById(testId);
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void save_shouldReturnNullWhenUserNotFound() {
        when(userRepository.findById(testId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class,
                () -> traineeService.save(testTraineeRequest));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(testId);
        verify(traineeRepository, never()).save(any(Trainee.class));
    }

    @Test
    void update_shouldUpdateExistingTrainee() throws NotFoundException {
        when(traineeRepository.findById(testId)).thenReturn(Optional.of(testTrainee));
        when(userRepository.findById(testId)).thenReturn(Optional.of(testUser));
        when(traineeRepository.update(eq(testId), any(Trainee.class))).thenReturn(testTrainee);

        var result = traineeService.update(testId, testTraineeUpdateRequest);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findById(testId);
        verify(userRepository).findById(testId);
        verify(traineeRepository).update(eq(testId), any(Trainee.class));
    }

    @Test
    void update_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findById(testId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class,
                () -> traineeService.update(testId, testTraineeUpdateRequest));
        assertEquals("Trainee not found", exception.getMessage());
        verify(traineeRepository).findById(testId);
        verify(traineeRepository, never()).update(anyString(), any(Trainee.class));
    }

    @Test
    void update_shouldReturnNullWhenUserNotFound() {
        when(traineeRepository.findById(testId)).thenReturn(Optional.of(testTrainee));
        when(userRepository.findById(testId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class,
                () -> traineeService.update(testId, testTraineeUpdateRequest));
        assertEquals("User not found", exception.getMessage());
        verify(traineeRepository).findById(testId);
        verify(userRepository).findById(testId);
        verify(traineeRepository, never()).update(anyString(), any(Trainee.class));
    }

    @Test
    void delete_shouldDeleteTrainee() {
        doNothing().when(traineeRepository).delete(testId);

        traineeService.delete(testId);

        verify(traineeRepository).delete(testId);
    }

    @Test
    void findAll_shouldReturnAllTrainees() {
        var trainees = Collections.singletonList(testTrainee);
        when(traineeRepository.findAll()).thenReturn(trainees);

        var result = traineeService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testId, result.getFirst().id());
        verify(traineeRepository).findAll();
    }

    @Test
    void findById_shouldReturnTraineeWhenFound() throws NotFoundException {
        when(traineeRepository.findById(testId)).thenReturn(Optional.of(testTrainee));

        var result = traineeService.findById(testId);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findById(testId);
    }

    @Test
    void findById_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findById(testId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class,
                () -> traineeService.findById(testId));
        assertEquals("User not found with this credentials", exception.getMessage());
        verify(traineeRepository).findById(testId);
    }

    @Test
    void changePassword_shouldUpdatePasswordSuccessfully() throws NotFoundException, CredentialException {
        when(traineeRepository.findById(testId)).thenReturn(Optional.of(testTrainee));
        when(userRepository.update(eq(testId), any(User.class))).thenReturn(testUser);

        var result = traineeService.changePassword(testId, testPassword, testNewPassword);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findById(testId);
        verify(userRepository).update(eq(testId), any(User.class));
    }

    @Test
    void changePassword_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findById(testId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class,
                () -> traineeService.changePassword(testId, testPassword, testNewPassword));
        assertEquals("Trainee not found with id " + testId, exception.getMessage());
        verify(traineeRepository).findById(testId);
        verify(userRepository, never()).update(anyString(), any(User.class));
    }

    @Test
    void changePassword_shouldReturnNullWhenOldPasswordDoesNotMatch() {
        when(traineeRepository.findById(testId)).thenReturn(Optional.of(testTrainee));

        var exception = assertThrows(CredentialException.class,
                () -> traineeService.changePassword(testId, "wrongPassword", testNewPassword));
        assertEquals("Old password do not match", exception.getMessage());
        verify(traineeRepository).findById(testId);
        verify(userRepository, never()).update(anyString(), any(User.class));
    }

    @Test
    void findByUsername_shouldReturnTraineeWhenFound() throws NotFoundException {
        when(traineeRepository.findByUsername(testUsername)).thenReturn(Optional.of(testTrainee));

        var result = traineeService.findByUsername(testUsername);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findByUsername(testUsername);
    }

    @Test
    void findByUsername_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class,
                () -> traineeService.findByUsername(testUsername));
        assertEquals("Trainee not found by username", exception.getMessage());
        verify(traineeRepository).findByUsername(testUsername);
    }

    @Test
    void deleteByUsername_shouldReturnId() throws NotFoundException {
        when(traineeRepository.deleteByUsername(testUsername)).thenReturn(testId);

        var result = traineeService.deleteByUsername(testUsername);

        assertEquals(testId, result);
        verify(traineeRepository).deleteByUsername(testUsername);
    }

    @Test
    void activateAction_shouldActivateTraineeUser() throws NotFoundException {
        when(traineeRepository.findByUsername(testUsername)).thenReturn(Optional.of(testTrainee));
        when(userRepository.update(eq(testId), any(User.class))).thenReturn(testUser);

        var result = traineeService.activateAction(testUsername);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findByUsername(testUsername);
        verify(userRepository).update(eq(testId), any(User.class));
    }

    @Test
    void deactivateAction_shouldDeactivateTraineeUser() throws NotFoundException {
        when(traineeRepository.findByUsername(testUsername)).thenReturn(Optional.of(testTrainee));
        when(userRepository.update(eq(testId), any(User.class))).thenReturn(testUser);

        var result = traineeService.deactivateAction(testUsername);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findByUsername(testUsername);
        verify(userRepository).update(eq(testId), any(User.class));
    }
}