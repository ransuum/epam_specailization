package org.epam.service;

import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.User;
import org.epam.models.dto.update.TraineeRequestDto;
import org.epam.repository.TraineeRepository;
import org.epam.repository.UserRepository;
import org.epam.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TraineeServiceTest {
    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private User testUsers;
    private Trainee testTrainee;
    private TraineeRequestDto testTraineeUpdateRequest;
    private final String testId = "test-id";
    private final String testUsername = "testuser";
    private final String testPassword = "password";
    private final String testNewPassword = "newpassword";

    @BeforeEach
    void setUp() {
        testUsers = new User();
        testUsers.setId(testId);
        testUsers.setUsername(testUsername);
        testUsers.setPassword(testPassword);
        testUsers.setIsActive(true);

        testTrainee = new Trainee();
        testTrainee.setId(testId);
        testTrainee.setUser(testUsers);
        testTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testTrainee.setAddress("Test Address");

        testTraineeUpdateRequest = new TraineeRequestDto();
        testTraineeUpdateRequest.setDateOfBirth("02-02-1991");
        testTraineeUpdateRequest.setAddress("Updated Address");

        setupSecurityContext();
    }

    private void setupSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(testUsername);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void update_shouldUpdateExistingTrainee() throws NotFoundException {
        when(traineeRepository.findByUser_Username(testUsername)).thenReturn(Optional.of(testTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        var result = traineeService.update(testTraineeUpdateRequest);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findByUser_Username(testUsername);
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void update_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findByUser_Username(testUsername)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class,
                () -> traineeService.update(testTraineeUpdateRequest));
        assertEquals("Trainee not found", exception.getMessage());
        verify(traineeRepository).findByUser_Username(testUsername);
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
        when(traineeRepository.findByUser_Username(testUsername)).thenReturn(Optional.of(testTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        var result = traineeService.changePassword(testPassword, testNewPassword);

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findByUser_Username(testUsername);
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void changePassword_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findByUser_Username(testUsername)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class,
                () -> traineeService.changePassword(testPassword, testNewPassword));
        assertEquals("Trainee not found with authUsername " + testUsername, exception.getMessage());
        verify(traineeRepository).findByUser_Username(testUsername);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_shouldReturnNullWhenOldPasswordDoesNotMatch() {
        when(traineeRepository.findByUser_Username(testUsername)).thenReturn(Optional.of(testTrainee));

        var exception = assertThrows(CredentialException.class,
                () -> traineeService.changePassword("wrongPassword", testNewPassword));
        assertEquals("Old password do not match", exception.getMessage());
        verify(traineeRepository).findByUser_Username(testUsername);
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
        testUsers.setIsActive(false);
        when(traineeRepository.findByUser_Username(testUsername)).thenReturn(Optional.of(testTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee updatedTrainee = invocation.getArgument(0);
            assertTrue(updatedTrainee.getUser().getIsActive());
            return updatedTrainee;
        });

        var result = traineeService.changeStatus();

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findByUser_Username(testUsername);
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void deactivateAction_shouldDeactivateTraineeUser() throws NotFoundException {
        testUsers.setIsActive(true);
        when(traineeRepository.findByUser_Username(testUsername)).thenReturn(Optional.of(testTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee updatedTrainee = invocation.getArgument(0);
            assertFalse(updatedTrainee.getUser().getIsActive());
            return updatedTrainee;
        });

        var result = traineeService.changeStatus();

        assertNotNull(result);
        assertEquals(testId, result.id());
        verify(traineeRepository).findByUser_Username(testUsername);
        verify(traineeRepository).save(any(Trainee.class));
    }
}