package org.epam.service;

import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.User;
import org.epam.models.dto.create.TraineeCreateDto;
import org.epam.models.dto.update.TraineeRequestDto;
import org.epam.repository.TraineeRepository;
import org.epam.repository.UserRepository;
import org.epam.service.impl.TraineeServiceImpl;
import org.epam.utils.CredentialsGenerator;
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
    private CredentialsGenerator credentialsGenerator;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private User testUser;
    private Trainee testTrainee;
    private TraineeCreateDto testTraineeRequest;
    private TraineeRequestDto testTraineeUpdateRequest;
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

        testTraineeRequest = new TraineeCreateDto(
                "John",
                "Doe",
                "01-01-1990",
                "Test Address"
        );

        testTraineeUpdateRequest = new TraineeRequestDto();
        testTraineeUpdateRequest.setDateOfBirth("02-02-1991");
        testTraineeUpdateRequest.setAddress("Updated Address");
    }

    @Test
    void save_shouldCreateNewTrainee() {
        String generatedUsername = "johndoe";
        String generatedPassword = "generated123";

        when(credentialsGenerator.generateUsername("John", "Doe")).thenReturn(generatedUsername);
        when(credentialsGenerator.generatePassword(generatedUsername)).thenReturn(generatedPassword);

        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee savedTrainee = invocation.getArgument(0);
            savedTrainee.setId(testId);
            return savedTrainee;
        });

        var result = traineeService.save(testTraineeRequest);

        assertNotNull(result);
        assertEquals(generatedUsername, result.username());
        assertEquals(generatedPassword, result.password());
        verify(credentialsGenerator).generateUsername("John", "Doe");
        verify(credentialsGenerator).generatePassword(generatedUsername);
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void update_shouldUpdateExistingTrainee() throws NotFoundException {
        when(traineeRepository.findById(testId)).thenReturn(Optional.of(testTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        var result = traineeService.update(testId, testTraineeUpdateRequest);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findById(testId);
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void update_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findById(testId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class,
                () -> traineeService.update(testId, testTraineeUpdateRequest));
        assertEquals("Trainee not found", exception.getMessage());
        verify(traineeRepository).findById(testId);
        verify(traineeRepository, never()).save(any(Trainee.class));
    }

    @Test
    void delete_shouldDeleteTrainee() {
        when(traineeRepository.findById(testId)).thenReturn(Optional.of(testTrainee));
        doNothing().when(traineeRepository).delete(testTrainee);

        traineeService.delete(testId);

        verify(traineeRepository).delete(testTrainee);
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
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        var result = traineeService.changePassword(testId, testPassword, testNewPassword);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findById(testId);
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void changePassword_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findById(testId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class,
                () -> traineeService.changePassword(testId, testPassword, testNewPassword));
        assertEquals("Trainee not found with id " + testId, exception.getMessage());
        verify(traineeRepository).findById(testId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_shouldReturnNullWhenOldPasswordDoesNotMatch() {
        when(traineeRepository.findById(testId)).thenReturn(Optional.of(testTrainee));

        var exception = assertThrows(CredentialException.class,
                () -> traineeService.changePassword(testId, "wrongPassword", testNewPassword));
        assertEquals("Old password do not match", exception.getMessage());
        verify(traineeRepository).findById(testId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByUsername_shouldReturnTraineeWhenFound() throws NotFoundException {
        when(traineeRepository.findByUser_Username(testUsername)).thenReturn(Optional.of(testTrainee));

        var result = traineeService.findByUsername(testUsername);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findByUser_Username(testUsername);
    }

    @Test
    void findByUsername_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findByUser_Username(testUsername)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class,
                () -> traineeService.findByUsername(testUsername));
        assertEquals("Trainee not found by username", exception.getMessage());
        verify(traineeRepository).findByUser_Username(testUsername);
    }

    @Test
    void deleteByUsername_shouldReturnId() throws NotFoundException {
        var result = traineeService.deleteByUsername(testUsername);

        assertEquals(testUsername, result);
        verify(traineeRepository).deleteByUser_Username(testUsername);
    }

    @Test
    void activateAction_shouldActivateTraineeUser() throws NotFoundException {
        testUser.setIsActive(false);
        when(traineeRepository.findByUser_Username(testUsername)).thenReturn(Optional.of(testTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee updatedTrainee = invocation.getArgument(0);
            assertTrue(updatedTrainee.getUser().getIsActive());
            return updatedTrainee;
        });

        var result = traineeService.changeStatus(testUsername);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findByUser_Username(testUsername);
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void deactivateAction_shouldDeactivateTraineeUser() throws NotFoundException {
        testUser.setIsActive(true);
        when(traineeRepository.findByUser_Username(testUsername)).thenReturn(Optional.of(testTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee updatedTrainee = invocation.getArgument(0);
            assertFalse(updatedTrainee.getUser().getIsActive());
            return updatedTrainee;
        });

        var result = traineeService.changeStatus(testUsername);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findByUser_Username(testUsername);
        verify(traineeRepository).save(any(Trainee.class));
    }
}