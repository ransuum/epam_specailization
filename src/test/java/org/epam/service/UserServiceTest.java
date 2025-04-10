package org.epam.service;

import org.epam.exception.NotFoundException;
import org.epam.models.entity.User;
import org.epam.models.dto.create.UserCreateDto;
import org.epam.repository.UserRepository;
import org.epam.service.impl.UserServiceImpl;
import org.epam.utils.CredentialsGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserCreateDto testUserRequest;
    private static final String TEST_ID = "test-id";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_USERNAME = "john.doe";
    private static final String TEST_PASSWORD = "Password123";
    private static final String TEST_FIRST_NAME_UPDATE = "John_UPDATE";
    private static final String TEST_LAST_NAME_UPDATE = "Doe_UPDATE";

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(TEST_ID)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .isActive(true)
                .build();

        testUserRequest = new UserCreateDto();
        testUserRequest.setFirstName(TEST_FIRST_NAME);
        testUserRequest.setLastName(TEST_LAST_NAME);
        testUserRequest.setIsActive(true);
    }

    @Test
    void save_shouldCreateNewUser() {
        when(credentialsGenerator.generateUsername(TEST_FIRST_NAME, TEST_LAST_NAME)).thenReturn(TEST_USERNAME);
        when(credentialsGenerator.generatePassword(TEST_USERNAME)).thenReturn(TEST_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        var result = userService.save(testUserRequest);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TEST_FIRST_NAME, result.firstName());
        assertEquals(TEST_LAST_NAME, result.lastName());
        assertEquals(TEST_USERNAME, result.username());
        assertTrue(result.isActive());
        assertEquals(TEST_PASSWORD, result.password());

        verify(credentialsGenerator).generateUsername(TEST_FIRST_NAME, TEST_LAST_NAME);
        verify(credentialsGenerator).generatePassword(TEST_USERNAME);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_shouldUpdateExistingUser() {
        var updatedUser = User.builder()
                .id(TEST_ID)
                .firstName("Jane")
                .lastName("Smith")
                .username("jane.smith")
                .password("NewPassword123")
                .isActive(false)
                .build();

        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(testUser));
        when(credentialsGenerator.generateUsername("Jane", "Smith")).thenReturn("jane.smith");
        when(credentialsGenerator.generatePassword("jane.smith")).thenReturn("NewPassword123");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        var result = userService.update(TEST_ID, updatedUser);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals("Jane", result.firstName());
        assertEquals("Smith", result.lastName());
        assertEquals("jane.smith", result.username());
        assertFalse(result.isActive());
        assertEquals("NewPassword123", result.password());

        verify(userRepository).findById(TEST_ID);
        verify(credentialsGenerator).generateUsername("Jane", "Smith");
        verify(credentialsGenerator).generatePassword("jane.smith");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_shouldReturnNullWhenUserNotFound() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        var updateRequest = new User(TEST_FIRST_NAME_UPDATE, TEST_LAST_NAME_UPDATE, Boolean.TRUE);

        var exception = assertThrows(NotFoundException.class, () ->
                userService.update(TEST_ID, updateRequest));

        assertEquals("User Not Found", exception.getMessage());
        verify(userRepository).findById(TEST_ID);
        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteUser() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        userService.delete(TEST_ID);

        verify(userRepository).delete(testUser);
    }

    @Test
    void findById_shouldReturnUserWhenFound() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(testUser));

        var result = userService.findById(TEST_ID);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TEST_FIRST_NAME, result.firstName());
        assertEquals(TEST_LAST_NAME, result.lastName());
        assertEquals(TEST_USERNAME, result.username());
        assertTrue(result.isActive());
        assertEquals(TEST_PASSWORD, result.password());

        verify(userRepository).findById(TEST_ID);
    }

    @Test
    void findById_shouldReturnNullWhenUserNotFound() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        var result = assertThrows(NotFoundException.class, () -> userService.findById(TEST_ID));

        assertEquals("User Not Found", result.getMessage());
        verify(userRepository).findById(TEST_ID);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateOnlyProvidedFields() {
        var partialUpdate = User.builder()
                .firstName("Jane")
                .lastName(TEST_LAST_NAME)
                .isActive(true)
                .build();

        var expectedUpdated = User.builder()
                .id(TEST_ID)
                .firstName("Jane")
                .lastName(TEST_LAST_NAME)
                .username("Jane.Doe")
                .password("NewPassword")
                .isActive(true)
                .build();

        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(testUser));
        when(credentialsGenerator.generateUsername("Jane", "Doe")).thenReturn("Jane.Doe");
        when(userRepository.save(any(User.class))).thenReturn(expectedUpdated);

        var result = userService.update(expectedUpdated.getId(), partialUpdate);

        assertNotNull(result);
        assertEquals("Jane", result.firstName());
        assertEquals(TEST_LAST_NAME, result.lastName());
        assertEquals("Jane.Doe", result.username());
        assertTrue(result.isActive());

        verify(userRepository).findById(TEST_ID);
        verify(credentialsGenerator).generateUsername("Jane", TEST_LAST_NAME);
        verify(userRepository).save(any(User.class));
    }
}