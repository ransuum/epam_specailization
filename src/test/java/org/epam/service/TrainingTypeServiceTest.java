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
import static org.mockito.ArgumentMatchers.eq;
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
        when(trainingTypeRepository.update(eq(testId), any(TrainingType.class))).thenReturn(updatedTrainingView);

        var result = trainingTypeService.update(testId, updatedTrainingTypeName);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(updatedTrainingTypeName.getVal(), result.trainingName());

        verify(trainingTypeRepository).findById(testId);
        verify(trainingTypeRepository).update(eq(testId), argThat(view ->
                view.getTrainingTypeName() == updatedTrainingTypeName));
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenTrainingViewNotFound() {
        when(trainingTypeRepository.findById(testId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () ->
                trainingTypeService.update(testId, updatedTrainingTypeName));

        assertEquals("Training type not found", exception.getMessage());
        verify(trainingTypeRepository).findById(testId);
        verify(trainingTypeRepository, never()).update(anyString(), any());
    }

    @Test
    void delete_shouldDeleteTrainingView() throws NotFoundException {
        doNothing().when(trainingTypeRepository).delete(testId);

        trainingTypeService.delete(testId);

        verify(trainingTypeRepository).delete(testId);
    }

    @Test
    void delete_shouldHandleNotFoundExceptionGracefully() throws NotFoundException {
        doThrow(new NotFoundException("Training view not found")).when(trainingTypeRepository).delete(testId);

        var exception = assertThrows(NotFoundException.class, () ->
                trainingTypeService.delete(testId));

        assertEquals("Training view not found", exception.getMessage());
        verify(trainingTypeRepository).delete(testId);
    }

    @Test
    void findAll_shouldReturnAllTrainingViews() throws NotFoundException {
        var secondTrainingView = TrainingType.builder()
                .id("test-id-456")
                .trainingTypeName(TrainingTypeName.SELF_PLACING)
                .trainings(Collections.emptyList())
                .trainers(Collections.emptyList())
                .build();

        var trainingViews = List.of(testTrainingTypeEntity, secondTrainingView);

        when(trainingTypeRepository.findAll()).thenReturn(trainingViews);

        var results = trainingTypeService.findAll();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(testId, results.get(0).id());
        assertEquals(testTrainingTypeName.getVal(), results.get(0).trainingName());
        assertEquals("test-id-456", results.get(1).id());
        assertEquals(TrainingTypeName.SELF_PLACING.getVal(), results.get(1).trainingName());

        verify(trainingTypeRepository).findAll();
    }

    @Test
    void findAll_shouldHandleEmptyList() throws NotFoundException {
        when(trainingTypeRepository.findAll()).thenReturn(Collections.emptyList());

        var results = trainingTypeService.findAll();

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(trainingTypeRepository).findAll();
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

        assertEquals("Training type not found", exception.getMessage());
        verify(trainingTypeRepository).findById(testId);
    }

}