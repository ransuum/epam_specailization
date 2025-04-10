package org.epam.service;

import org.epam.exception.NotFoundException;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.entity.TrainingType;
import org.epam.models.enums.TrainingTypeName;
import org.epam.repository.TrainingTypeRepository;
import org.epam.service.impl.TrainingTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    private TrainingType testTrainingTypeEntity;
    private String testId;
    private TrainingTypeName testTrainingTypeName;
    private TrainingTypeName updatedTrainingTypeName;
    private List<Training> testTrainings;
    private List<Trainer> testTrainers;

    @BeforeEach
    void setUp() {
        testId = "test-id-123";
        testTrainingTypeName = TrainingTypeName.LABORATORY;
        updatedTrainingTypeName = TrainingTypeName.FUNDAMENTALS;

        var training1 = new Training();
        training1.setId("training-1");
        var training2 = new Training();
        training2.setId("training-2");
        testTrainings = List.of(training1, training2);

        var trainer1 = new Trainer();
        trainer1.setId("trainer-1");
        var trainer2 = new Trainer();
        trainer2.setId("trainer-2");
        testTrainers = List.of(trainer1, trainer2);

        testTrainingTypeEntity = TrainingType.builder()
                .id(testId)
                .trainingTypeName(testTrainingTypeName)
                .trainings(testTrainings)
                .trainers(testTrainers)
                .build();
    }

    @Test
    void save_shouldCreateNewTrainingView() {
        when(trainingTypeRepository.save(any(TrainingType.class))).thenReturn(testTrainingTypeEntity);

        var result = trainingTypeService.save(testTrainingTypeName);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testTrainingTypeName.getVal(), result.trainingName());

        verify(trainingTypeRepository).save(argThat(view ->
                view.getTrainingTypeName() == testTrainingTypeName));
    }

    @Test
    void update_shouldUpdateTrainingType() throws NotFoundException {
        var updatedTrainingView = TrainingType.builder()
                .id(testId)
                .trainingTypeName(updatedTrainingTypeName)
                .trainings(testTrainings)
                .trainers(testTrainers)
                .build();

        when(trainingTypeRepository.findById(testId)).thenReturn(Optional.of(testTrainingTypeEntity));
        when(trainingTypeRepository.save(any(TrainingType.class))).thenReturn(updatedTrainingView);

        var result = trainingTypeService.update(testId, updatedTrainingTypeName);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(updatedTrainingTypeName.getVal(), result.trainingName());

        verify(trainingTypeRepository).findById(testId);
        verify(trainingTypeRepository).save(argThat(view ->
                view.getTrainingTypeName() == updatedTrainingTypeName));
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenTrainingViewNotFound() {
        when(trainingTypeRepository.findById(testId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () ->
                trainingTypeService.update(testId, updatedTrainingTypeName));

        assertEquals("Training Type Not Found", exception.getMessage());
        verify(trainingTypeRepository).findById(testId);
        verify(trainingTypeRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteTrainingView() throws NotFoundException {
        doNothing().when(trainingTypeRepository).deleteById(testId);

        trainingTypeService.delete(testId);

        verify(trainingTypeRepository).deleteById(testId);
    }

    @Test
    void delete_shouldHandleNotFoundExceptionGracefully() throws NotFoundException {
        doThrow(new NotFoundException("Training view not found")).when(trainingTypeRepository).deleteById(testId);

        var exception = assertThrows(NotFoundException.class, () ->
                trainingTypeService.delete(testId));

        assertEquals("Training view not found", exception.getMessage());
        verify(trainingTypeRepository).deleteById(testId);
    }

    @Test
    void findById_shouldReturnTrainingViewWhenFound() throws NotFoundException {
        when(trainingTypeRepository.findById(testId)).thenReturn(Optional.of(testTrainingTypeEntity));

        var result = trainingTypeService.findById(testId);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testTrainingTypeName.getVal(), result.trainingName());

        verify(trainingTypeRepository).findById(testId);
    }

    @Test
    void findById_shouldThrowNotFoundExceptionWhenNotFound() {
        when(trainingTypeRepository.findById(testId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () ->
                trainingTypeService.findById(testId));

        assertEquals("Training Type Not Found", exception.getMessage());
        verify(trainingTypeRepository).findById(testId);
    }

}