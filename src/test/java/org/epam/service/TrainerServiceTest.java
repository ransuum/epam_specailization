package org.epam.service;

import org.epam.models.entity.Trainer;
import org.epam.repository.TrainerRepo;
import org.epam.service.impl.TrainerServiceImpl;
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
    private TrainerRepo trainerRepo;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void testSaveTrainer_success() {
        Trainer trainer = new Trainer("Java", "John", "Doe", "john.doe", "Password1@", Boolean.TRUE);
        when(trainerRepo.save(trainer)).thenReturn(trainer);

        Trainer savedTrainer = trainerService.save(trainer);

        assertNotNull(savedTrainer);
        assertEquals("John", savedTrainer.getFirstName());
        verify(trainerRepo, times(1)).save(trainer);
    }

    @Test
    void testUpdateTrainer_success() {
        int trainerId = 1;
        Trainer existingTrainer = new Trainer("Java", "John", "Doe", "john.doe", "OldPass1@", Boolean.TRUE);
        existingTrainer.setId(trainerId);

        Trainer updateData = new Trainer("Java", "Johnny", "Doe", "john.doe", "NewPass@", Boolean.TRUE);
        updateData.setId(trainerId);

        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(existingTrainer));
        when(trainerRepo.update(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer updatedTrainer = trainerService.update(updateData);

        assertEquals("Johnny", updatedTrainer.getFirstName());
        assertEquals("NewPass@", updatedTrainer.getPassword());
        verify(trainerRepo, times(1)).update(any(Trainer.class));
    }

    @Test
    void testUpdateTrainer_notFound() {
        int trainerId = 1;
        Trainer updateData = new Trainer("Java", "John", "Doe", "john.doe", "NewPass@", Boolean.TRUE);
        updateData.setId(trainerId);

        when(trainerRepo.findById(trainerId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> trainerService.update(updateData));
        assertEquals("Trainer not found", exception.getMessage());
    }

    @Test
    void testDeleteTrainer_success() {
        int trainerId = 1;
        Trainer trainer = new Trainer("Java", "John", "Doe", "john.doe", "Password@", Boolean.TRUE);

        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(trainer));
        doNothing().when(trainerRepo).delete(trainerId);

        trainerService.delete(trainerId);

        verify(trainerRepo, times(1)).delete(trainerId);
    }

    @Test
    void testFindAllTrainers() {
        Trainer trainer1 = new Trainer("Java", "John", "Doe", "john.doe", "Password@", Boolean.TRUE);
        Trainer trainer2 = new Trainer("Java", "Jane", "Doe", "jane.doe", "Password@", Boolean.TRUE);
        List<Trainer> trainers = Arrays.asList(trainer1, trainer2);

        when(trainerRepo.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.findAll();

        assertEquals(2, result.size());
        verify(trainerRepo, times(1)).findAll();
    }

    @Test
    void testFindTrainerById_success() {
        int trainerId = 1;
        Trainer trainer = new Trainer("Java", "John", "Doe", "john.doe", "Password@", Boolean.TRUE);
        trainer.setId(trainerId);

        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.findById(trainerId);

        assertNotNull(result);
        assertEquals(trainerId, result.getId());
        verify(trainerRepo, times(1)).findById(trainerId);
    }

    @Test
    void testFindTrainerById_notFound() {
        int trainerId = 1;

        when(trainerRepo.findById(trainerId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> trainerService.findById(trainerId));
        assertEquals("Trainer not found", exception.getMessage());
    }
}
