package org.epam.service;

import org.epam.models.entity.Trainer;
import org.epam.repository.TrainerRepo;
import org.epam.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {
    private TrainerRepo trainerRepo;
    private TrainerService trainerService;

    @BeforeEach
    public void setUp() {
        trainerRepo = Mockito.mock(TrainerRepo.class);
        trainerService = new TrainerServiceImpl(trainerRepo);
    }

    @Test
    public void testSaveTrainer_success() {
        Trainer trainer = new Trainer("Fitness", "Alex", "Johnson", "alex.j", "Fitness123@", Boolean.TRUE);
        when(trainerRepo.save(trainer)).thenReturn(trainer);

        Trainer savedTrainer = trainerService.save(trainer);

        assertNotNull(savedTrainer);
        assertEquals("Alex", savedTrainer.getFirstName());
        verify(trainerRepo, times(1)).save(trainer);
    }

    @Test
    public void testUpdateTrainer_success() {
        int trainerId = 1;
        Trainer existingTrainer = new Trainer("Yoga", "Sarah", "Miller", "sarah.m", "Yoga456@", Boolean.TRUE);
        Trainer updateData = new Trainer(1, "Yoga", "Sara", "Miller", "sarah.m", "Yoga789@", Boolean.TRUE);

        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(existingTrainer));
        when(trainerRepo.update(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer updatedTrainer = trainerService.update(updateData);

        assertEquals("Sara", updatedTrainer.getFirstName());
        assertEquals("Yoga789@", updatedTrainer.getPassword());
        verify(trainerRepo, times(1)).update(any(Trainer.class));
    }

    @Test
    public void testDeleteTrainer_success() {
        int trainerId = 1;
        Trainer trainer = new Trainer("Boxing", "Anna", "Lee", "anna.l", "Box101@", Boolean.TRUE);
        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(trainer));
        doNothing().when(trainerRepo).delete(trainerId);

        trainerService.delete(trainerId);

        verify(trainerRepo, times(1)).delete(trainerId);
    }

    @Test
    public void testFindAllTrainers() {
        Trainer trainer1 = new Trainer("Fitness", "Alex", "Johnson", "alex.j", "Fitness123@", Boolean.TRUE);
        Trainer trainer2 = new Trainer("Nutrition", "Robert", "Wilson", "robert.w", "Nutri202@", Boolean.TRUE);
        List<Trainer> trainers = Arrays.asList(trainer1, trainer2);
        when(trainerRepo.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.findAll();

        assertEquals(2, result.size());
        verify(trainerRepo, times(1)).findAll();
    }

    @Test
    public void testFindTrainerById_success() {
        int trainerId = 1;
        Trainer trainer = new Trainer("Crossfit", "Mike", "Thompson", "mike.t", "Cross789@", Boolean.TRUE);
        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.findById(trainerId);

        assertNotNull(result);
        assertEquals(trainerId, result.getId());
        verify(trainerRepo, times(1)).findById(trainerId);
    }
}