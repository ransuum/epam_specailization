package org.epam.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.epam.models.dto.TrainerDto;
import org.epam.models.entity.Trainer;
import org.epam.repository.TrainerRepository;
import org.epam.service.impl.TrainerServiceImpl;
import org.epam.util.CredentialsGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {
    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void testSaveTrainer_success() {
        Trainer trainer = new Trainer("Java", "John", "Doe", "johndoe", "password", true);
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        TrainerDto dummyDto = mock(TrainerDto.class);
        when(objectMapper.convertValue(trainer, TrainerDto.class)).thenReturn(dummyDto);

        TrainerDto result = trainerService.save(trainer);

        assertEquals(dummyDto, result);
        verify(trainerRepository, times(1)).save(trainer);
    }

    @Test
    void testUpdateTrainer_success() {
        Integer id = 1;
        String username = CredentialsGenerator.generateUsername("John", "Doe");
        String password = CredentialsGenerator.generatePassword(username);
        Trainer existingTrainer = new Trainer("Java", "John", "Doe", username, password, true);
        String newUsername = CredentialsGenerator.generateUsername("Jane", "Smith");
        String newPassword = CredentialsGenerator.generatePassword(newUsername);
        Trainer updateInfo = new Trainer("Python", "Jane", "Smith", newUsername, newPassword, true);

        when(trainerRepository.findById(id)).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.update(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerDto dummyDto = mock(TrainerDto.class);
        when(objectMapper.convertValue(existingTrainer, TrainerDto.class)).thenReturn(dummyDto);

        TrainerDto result = trainerService.update(id, updateInfo);

        assertEquals("Jane", existingTrainer.getFirstName());
        assertEquals("Smith", existingTrainer.getLastName());
        assertEquals(newUsername, existingTrainer.getUsername());
        assertEquals("Python", existingTrainer.getSpecialization());
        assertEquals(dummyDto, result);

        verify(trainerRepository, times(1)).findById(id);
        verify(trainerRepository, times(1)).update(existingTrainer);
    }

    @Test
    void testUpdateTrainer_notFound() {
        Integer id = 1;
        Trainer updateInfo = new Trainer("Python", "Jane", "Smith", "janesmith", "newpass", true);

        when(trainerRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> trainerService.update(id, updateInfo));
        assertEquals("Trainer not found", exception.getMessage());
        verify(trainerRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteTrainer_success() {
        Integer id = 1;
        Trainer trainer = new Trainer("Java", "John", "Doe", "johndoe", "password", true);

        TrainerDto dummyDto = mock(TrainerDto.class);
        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));
        when(objectMapper.convertValue(trainer, TrainerDto.class)).thenReturn(dummyDto);
        when(dummyDto.username()).thenReturn("johndoe");

        trainerService.delete(id);

        verify(trainerRepository, times(1)).delete(id);
    }

    @Test
    void testFindAllTrainers() {
        Trainer trainer1 = new Trainer("Java", "John", "Doe", "johndoe", "password", true);
        Trainer trainer2 = new Trainer("Python", "Alice", "Smith", "alicesmith", "pass", true);
        List<Trainer> trainers = Arrays.asList(trainer1, trainer2);

        TrainerDto dto1 = mock(TrainerDto.class);
        TrainerDto dto2 = mock(TrainerDto.class);

        when(trainerRepository.findAll()).thenReturn(trainers);
        when(objectMapper.convertValue(trainer1, TrainerDto.class)).thenReturn(dto1);
        when(objectMapper.convertValue(trainer2, TrainerDto.class)).thenReturn(dto2);

        List<TrainerDto> result = trainerService.findAll();
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(dto1, dto2)));
        verify(trainerRepository, times(1)).findAll();
    }

    @Test
    void testFindTrainerById_success() {
        Integer id = 1;
        Trainer trainer = new Trainer("Java", "John", "Doe", "johndoe", "password", true);

        TrainerDto dummyDto = mock(TrainerDto.class);
        when(trainerRepository.findById(id)).thenReturn(Optional.of(trainer));
        when(objectMapper.convertValue(trainer, TrainerDto.class)).thenReturn(dummyDto);

        TrainerDto result = trainerService.findById(id);
        assertEquals(dummyDto, result);
        verify(trainerRepository, times(1)).findById(id);
    }

    @Test
    void testFindTrainerById_notFound() {
        Integer id = 1;
        when(trainerRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> trainerService.findById(id));
        assertEquals("Trainer not found", exception.getMessage());
        verify(trainerRepository, times(1)).findById(id);
    }
}
