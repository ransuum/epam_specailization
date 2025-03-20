package org.epam.service;

import org.epam.exception.NotFoundException;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.User;
import org.epam.models.request.traineerequest.TraineeRequestCreate;
import org.epam.models.request.traineerequest.TraineeRequestUpdate;
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
import java.util.List;
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
    private static final String TEST_ID = "test-id";
    private static final String TEST_USER_ID = "test-user-id";
    private static final String TEST_USERNAME = "john.doe";
    private static final String TEST_ADDRESS = "123 Test St";
    private static final LocalDate TEST_DOB = LocalDate.of(1990, 1, 1);
    private static final String OLD_PASSWORD = "oldPass";
    private static final String NEW_PASSWORD = "newPass";

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(TEST_USER_ID)
                .firstName("John")
                .lastName("Doe")
                .username(TEST_USERNAME)
                .password(OLD_PASSWORD)
                .isActive(true)
                .build();

        testTrainee = Trainee.builder()
                .id(TEST_ID)
                .user(testUser)
                .dateOfBirth(TEST_DOB)
                .address(TEST_ADDRESS)
                .build();

        testTraineeRequest = new TraineeRequestCreate(TEST_USER_ID, TEST_DOB, TEST_ADDRESS);

        testTraineeUpdateRequest = new TraineeRequestUpdate();
        testTraineeUpdateRequest.setUserId(TEST_USER_ID);
        testTraineeUpdateRequest.setDateOfBirth(TEST_DOB);
        testTraineeUpdateRequest.setAddress(TEST_ADDRESS);
    }

    @Test
    void save_shouldCreateNewTrainee() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        var result = traineeService.save(testTraineeRequest);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TEST_DOB, result.dateOfBirth());
        assertEquals(TEST_ADDRESS, result.address());
        assertNotNull(result.user());
        assertEquals(TEST_USER_ID, result.user().id());

        verify(userRepository).findById(TEST_USER_ID);
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void save_shouldReturnNullWhenUserNotFound() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        var result = traineeService.save(testTraineeRequest);

        assertNull(result);
        verify(userRepository).findById(TEST_USER_ID);
        verifyNoInteractions(traineeRepository);
    }

    @Test
    void update_shouldUpdateExistingTrainee() {
        var updateRequest = new TraineeRequestUpdate();
        updateRequest.setAddress("New Address");
        updateRequest.setDateOfBirth(LocalDate.of(1995, 5, 5));
        updateRequest.setUserId(TEST_USER_ID);

        var updatedTrainee = Trainee.builder()
                .id(TEST_ID)
                .user(testUser)
                .dateOfBirth(LocalDate.of(1995, 5, 5))
                .address("New Address")
                .build();

        when(traineeRepository.findById(TEST_ID)).thenReturn(Optional.of(testTrainee));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(traineeRepository.update(eq(TEST_ID), any(Trainee.class))).thenReturn(updatedTrainee);

        var result = traineeService.update(TEST_ID, updateRequest);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(LocalDate.of(1995, 5, 5), result.dateOfBirth());
        assertEquals("New Address", result.address());
        assertNotNull(result.user());
        assertEquals(TEST_USER_ID, result.user().id());

        verify(traineeRepository).findById(TEST_ID);
        verify(userRepository).findById(TEST_USER_ID);
        verify(traineeRepository).update(eq(TEST_ID), any(Trainee.class));
    }

    @Test
    void update_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        var result = traineeService.update(TEST_ID, testTraineeUpdateRequest);

        assertNull(result);
        verify(traineeRepository).findById(TEST_ID);
        verifyNoMoreInteractions(traineeRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void update_shouldReturnNullWhenUserNotFound() {
        when(traineeRepository.findById(TEST_ID)).thenReturn(Optional.of(testTrainee));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        var result = traineeService.update(TEST_ID, testTraineeUpdateRequest);

        assertNull(result);
        verify(traineeRepository).findById(TEST_ID);
        verify(userRepository).findById(TEST_USER_ID);
        verifyNoMoreInteractions(traineeRepository);
    }

    @Test
    void delete_shouldDeleteTrainee() {
        doNothing().when(traineeRepository).delete(TEST_ID);

        traineeService.delete(TEST_ID);

        verify(traineeRepository).delete(TEST_ID);
    }

    @Test
    void delete_shouldHandleNotFoundExceptionGracefully() {
        doThrow(new NotFoundException("Trainee not found")).when(traineeRepository).delete(TEST_ID);

        traineeService.delete(TEST_ID);

        verify(traineeRepository).delete(TEST_ID);
    }

    @Test
    void findAll_shouldReturnAllTrainees() {
        List<Trainee> trainees = List.of(testTrainee);
        when(traineeRepository.findAll()).thenReturn(trainees);

        var result = traineeService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TEST_ID, result.getFirst().id());
        assertEquals(TEST_DOB, result.getFirst().dateOfBirth());
        assertEquals(TEST_ADDRESS, result.getFirst().address());
        assertEquals(TEST_USER_ID, result.getFirst().user().id());

        verify(traineeRepository).findAll();
    }

    @Test
    void findById_shouldReturnTraineeWhenFound() {
        when(traineeRepository.findById(TEST_ID)).thenReturn(Optional.of(testTrainee));

        var result = traineeService.findById(TEST_ID);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TEST_DOB, result.dateOfBirth());
        assertEquals(TEST_ADDRESS, result.address());
        assertEquals(TEST_USER_ID, result.user().id());

        verify(traineeRepository).findById(TEST_ID);
    }

    @Test
    void findById_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        var result = traineeService.findById(TEST_ID);

        assertNull(result);
        verify(traineeRepository).findById(TEST_ID);
    }

    @Test
    void changePassword_shouldUpdatePasswordSuccessfully() {
        when(traineeRepository.findById(TEST_ID)).thenReturn(Optional.of(testTrainee));

        when(userRepository.update(eq(TEST_USER_ID), any(User.class))).thenReturn(testTrainee.getUser());

        var result = traineeService.changePassword(TEST_ID, OLD_PASSWORD, NEW_PASSWORD);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());

        verify(traineeRepository).findById(TEST_ID);
        verify(userRepository).update(eq(TEST_USER_ID), any(User.class));
    }

    @Test
    void changePassword_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        var result = traineeService.changePassword(TEST_ID, OLD_PASSWORD, NEW_PASSWORD);

        assertNull(result);
        verify(traineeRepository).findById(TEST_ID);
        verifyNoInteractions(userRepository);
    }

    @Test
    void changePassword_shouldReturnNullWhenOldPasswordDoesNotMatch() {
        when(traineeRepository.findById(TEST_ID)).thenReturn(Optional.of(testTrainee));

        var result = traineeService.changePassword(TEST_ID, "wrongPassword", NEW_PASSWORD);

        assertNull(result);
        verify(traineeRepository).findById(TEST_ID);
        verifyNoInteractions(userRepository);
    }

    @Test
    void findByUsername_shouldReturnTraineeWhenFound() {
        when(traineeRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testTrainee));

        var result = traineeService.findByUsername(TEST_USERNAME);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TEST_DOB, result.dateOfBirth());
        assertEquals(TEST_ADDRESS, result.address());
        assertEquals(TEST_USER_ID, result.user().id());

        verify(traineeRepository).findByUsername(TEST_USERNAME);
    }

    @Test
    void findByUsername_shouldReturnNullWhenTraineeNotFound() {
        when(traineeRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        var result = traineeService.findByUsername(TEST_USERNAME);

        assertNull(result);
        verify(traineeRepository).findByUsername(TEST_USERNAME);
    }

    @Test
    void deleteByUsername_shouldReturnId() {
        when(traineeRepository.deleteByUsername(TEST_USERNAME)).thenReturn(TEST_ID);

        String result = traineeService.deleteByUsername(TEST_USERNAME);

        assertEquals(TEST_ID, result);
        verify(traineeRepository).deleteByUsername(TEST_USERNAME);
    }

    @Test
    void activateAction_shouldActivateTraineeUser() {
        when(traineeRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testTrainee));
        when(userRepository.update(eq(TEST_USER_ID), any(User.class))).thenReturn(testUser);

        var result = traineeService.activateAction(TEST_USERNAME);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertTrue(result.user().isActive());

        verify(traineeRepository).findByUsername(TEST_USERNAME);
        verify(userRepository).update(eq(TEST_USER_ID), any(User.class));
    }

    @Test
    void deactivateAction_shouldDeactivateTraineeUser() {
        var inactiveUser = User.builder()
                .id(TEST_USER_ID)
                .firstName("John")
                .lastName("Doe")
                .username(TEST_USERNAME)
                .password(OLD_PASSWORD)
                .isActive(false)
                .build();

        var inactiveTrainee = Trainee.builder()
                .id(TEST_ID)
                .user(inactiveUser)
                .dateOfBirth(TEST_DOB)
                .address(TEST_ADDRESS)
                .build();

        when(traineeRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testTrainee));
        when(userRepository.update(eq(TEST_USER_ID), any(User.class))).thenReturn(inactiveUser);

        var result = traineeService.deactivateAction(TEST_USERNAME);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertFalse(result.user().isActive());

        verify(traineeRepository).findByUsername(TEST_USERNAME);
        verify(userRepository).update(eq(TEST_USER_ID), any(User.class));
    }
}