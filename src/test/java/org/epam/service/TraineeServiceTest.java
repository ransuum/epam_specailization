package org.epam.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.epam.models.dto.TraineeDto;
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

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void testSaveTrainee_success() {
        Trainee trainee = new Trainee("123 Main St", LocalDate.of(1990, 1, 1),
                "John", "Doe", "jdoe", "password", true);

        when(traineeRepo.save(trainee)).thenReturn(trainee);

        TraineeDto dummyDto = mock(TraineeDto.class);
        when(objectMapper.convertValue(trainee, TraineeDto.class)).thenReturn(dummyDto);

        TraineeDto result = traineeService.save(trainee);

        assertEquals(dummyDto, result);
        verify(traineeRepo, times(1)).save(trainee);
    }

    @Test
    void testUpdateTrainee_success() {
        Integer id = 1;
        Trainee existingTrainee = new Trainee("123 Main St", LocalDate.of(1990, 1, 1),
                "John", "Doe", "jdoe", "password", true);
        Trainee updateInfo = new Trainee("456 New Address", LocalDate.of(1991, 2, 2),
                "Jane", "Doe", "janedoe", "newpassword", true);

        when(traineeRepo.findById(id)).thenReturn(Optional.of(existingTrainee));
        when(traineeRepo.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TraineeDto dummyDto = mock(TraineeDto.class);
        when(objectMapper.convertValue(existingTrainee, TraineeDto.class)).thenReturn(dummyDto);

        TraineeDto result = traineeService.update(id, updateInfo);

        assertEquals("456 New Address", existingTrainee.getAddress());
        assertEquals("newpassword", existingTrainee.getPassword());
        assertEquals("janedoe", existingTrainee.getUsername());
        assertEquals("Jane", existingTrainee.getFirstName());
        assertEquals("Doe", existingTrainee.getLastName());
        assertEquals(LocalDate.of(1991, 2, 2), existingTrainee.getDateOfBirth());
        assertEquals(dummyDto, result);
        verify(traineeRepo, times(1)).findById(id);
        verify(traineeRepo, times(1)).update(existingTrainee);
    }

    @Test
    void testUpdateTrainee_notFound() {
        Integer id = 1;
        Trainee updateInfo = new Trainee("456 New Address", LocalDate.of(1991, 2, 2),
                "Jane", "Doe", "janedoe", "newpassword", true);
        when(traineeRepo.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> traineeService.update(id, updateInfo));
        assertEquals("Trainee not found", exception.getMessage());
        verify(traineeRepo, times(1)).findById(id);
    }

    @Test
    void testDeleteTrainee_success() {
        Integer id = 1;
        Trainee trainee = new Trainee("123 Main St", LocalDate.of(1990, 1, 1),
                "John", "Doe", "jdoe", "password", true);
        TraineeDto dummyDto = mock(TraineeDto.class);
        when(dummyDto.username()).thenReturn("jdoe");
        when(traineeRepo.findById(id)).thenReturn(Optional.of(trainee));
        when(objectMapper.convertValue(trainee, TraineeDto.class)).thenReturn(dummyDto);

        traineeService.delete(id);

        verify(traineeRepo, times(1)).delete(id);
    }

    @Test
    void testFindAllTrainees() {
        Trainee trainee1 = new Trainee("123 Main St", LocalDate.of(1990, 1, 1),
                "John", "Doe", "jdoe", "password", true);
        Trainee trainee2 = new Trainee("456 Side St", LocalDate.of(1992, 3, 4),
                "Alice", "Smith", "asmith", "pass", true);
        List<Trainee> trainees = Arrays.asList(trainee1, trainee2);

        TraineeDto dto1 = mock(TraineeDto.class);
        TraineeDto dto2 = mock(TraineeDto.class);

        when(traineeRepo.findAll()).thenReturn(trainees);
        when(objectMapper.convertValue(trainee1, TraineeDto.class)).thenReturn(dto1);
        when(objectMapper.convertValue(trainee2, TraineeDto.class)).thenReturn(dto2);

        List<TraineeDto> result = traineeService.findAll();
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(dto1, dto2)));
        verify(traineeRepo, times(1)).findAll();
    }

    @Test
    void testFindTraineeById_success() {
        Integer id = 1;
        Trainee trainee = new Trainee("123 Main St", LocalDate.of(1990, 1, 1),
                "John", "Doe", "jdoe", "password", true);
        TraineeDto dummyDto = mock(TraineeDto.class);
        when(traineeRepo.findById(id)).thenReturn(Optional.of(trainee));
        when(objectMapper.convertValue(trainee, TraineeDto.class)).thenReturn(dummyDto);

        TraineeDto result = traineeService.findById(id);
        assertEquals(dummyDto, result);
        verify(traineeRepo, times(1)).findById(id);
    }

    @Test
    void testFindTraineeById_notFound() {
        Integer id = 1;
        when(traineeRepo.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> traineeService.findById(id));
        assertEquals("Trainee not found", exception.getMessage());
        verify(traineeRepo, times(1)).findById(id);
    }
}
