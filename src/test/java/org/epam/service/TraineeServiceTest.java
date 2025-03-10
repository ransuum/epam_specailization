package org.epam.service;

import org.epam.models.entity.Trainee;
import org.epam.repository.TraineeRepo;
import org.epam.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
    @Mock
    private TraineeRepo traineeRepo;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void testSaveTrainee_success() {
        Trainee trainee = new Trainee("address", LocalDate.now(), "John", "Doe", "john.doe", "Password1@", Boolean.TRUE);
        when(traineeRepo.save(trainee)).thenReturn(trainee);

        Trainee savedTrainee = traineeService.save(trainee);

        assertNotNull(savedTrainee);
        assertEquals("John", savedTrainee.getFirstName());
        verify(traineeRepo, times(1)).save(trainee);
    }

    @Test
    void testUpdateTrainee_success() {
        int traineeId = 1;
        Trainee existingTrainee = new Trainee("Old Address", LocalDate.of(2000, 1, 1), "John", "Doe", "john.doe", "OldPass@", Boolean.TRUE);
        existingTrainee.setId(traineeId);

        Trainee updateData = new Trainee("New Address", LocalDate.of(1999, 5, 10), "Johnny", "Doe", "johnny.d", "NewPass@", Boolean.TRUE);
        updateData.setId(traineeId);

        when(traineeRepo.findById(traineeId)).thenReturn(Optional.of(existingTrainee));
        when(traineeRepo.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee updatedTrainee = traineeService.update(updateData);

        assertEquals("Johnny", updatedTrainee.getFirstName());
        assertEquals("New Address", updatedTrainee.getAddress());
        assertEquals("NewPass@", updatedTrainee.getPassword());
        verify(traineeRepo, times(1)).update(any(Trainee.class));
    }

    @Test
    void testUpdateTrainee_notFound() {
        int traineeId = 1;
        Trainee updateData = new Trainee("New Address", LocalDate.now(), "Johnny", "Doe", "johnny.d", "NewPass@", Boolean.TRUE);
        updateData.setId(traineeId);

        when(traineeRepo.findById(traineeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> traineeService.update(updateData));
        assertEquals("Trainee not found", exception.getMessage());
    }

    @Test
    void testDeleteTrainee_success() {
        int traineeId = 1;
        Trainee trainee = new Trainee("address", LocalDate.now(), "John", "Doe", "john.doe", "Password1@", Boolean.TRUE);

        when(traineeRepo.findById(traineeId)).thenReturn(Optional.of(trainee));
        doNothing().when(traineeRepo).delete(traineeId);

        traineeService.delete(traineeId);

        verify(traineeRepo, times(1)).delete(traineeId);
    }

    @Test
    void testFindAllTrainees() {
        Trainee trainee1 = new Trainee("address1", LocalDate.now(), "John", "Doe", "john.doe", "Password1@", Boolean.TRUE);
        Trainee trainee2 = new Trainee("address2", LocalDate.now(), "Jane", "Doe", "jane.doe", "Password2@", Boolean.TRUE);
        List<Trainee> trainees = Arrays.asList(trainee1, trainee2);

        when(traineeRepo.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.findAll();

        assertEquals(2, result.size());
        verify(traineeRepo, times(1)).findAll();
    }

    @Test
    void testFindTraineeById_success() {
        int traineeId = 1;
        Trainee trainee = new Trainee("address", LocalDate.now(), "John", "Doe", "john.doe", "Password1@", Boolean.TRUE);
        trainee.setId(traineeId);

        when(traineeRepo.findById(traineeId)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.findById(traineeId);

        assertNotNull(result);
        assertEquals(traineeId, result.getId());
        verify(traineeRepo, times(1)).findById(traineeId);
    }

    @Test
    void testFindTraineeById_notFound() {
        int traineeId = 1;

        when(traineeRepo.findById(traineeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> traineeService.findById(traineeId));
        assertEquals("Trainee not found", exception.getMessage());
    }
}
