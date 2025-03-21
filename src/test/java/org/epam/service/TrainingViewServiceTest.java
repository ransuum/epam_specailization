package org.epam.service;

import org.epam.exception.NotFoundException;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.entity.TrainingView;
import org.epam.models.enums.TrainingType;
import org.epam.repository.TrainingViewRepository;
import org.epam.service.impl.TrainingViewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingViewServiceTest {

    @Mock
    private TrainingViewRepository trainingViewRepository;

    @InjectMocks
    private TrainingViewServiceImpl trainingViewService;

    private TrainingView testTrainingView;
    private String testId;
    private TrainingType testTrainingType;
    private TrainingType updatedTrainingType;
    private List<Training> testTrainings;
    private List<Trainer> testTrainers;

    @BeforeEach
    void setUp() {
        testId = "test-id-123";
        testTrainingType = TrainingType.LABORATORY;
        updatedTrainingType = TrainingType.FUNDAMENTALS;

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

        testTrainingView = TrainingView.builder()
                .id(testId)
                .trainingType(testTrainingType)
                .trainings(testTrainings)
                .trainers(testTrainers)
                .build();
    }

    @Test
    void save_shouldCreateNewTrainingView() {
        when(trainingViewRepository.save(any(TrainingView.class))).thenReturn(testTrainingView);

        var result = trainingViewService.save(testTrainingType);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testTrainingType, result.trainingType());

        var expectedTrainingIds = testTrainings.stream()
                .map(Training::getId)
                .collect(Collectors.toList());
        assertEquals(expectedTrainingIds, result.trainingsIds());

        var expectedSpecializationIds = testTrainers.stream()
                .map(Trainer::getId)
                .collect(Collectors.toList());
        assertEquals(expectedSpecializationIds, result.specializationIds());

        verify(trainingViewRepository).save(argThat(view ->
                view.getTrainingType() == testTrainingType));
    }

    @Test
    void update_shouldUpdateTrainingType() throws NotFoundException {
        var updatedTrainingView = TrainingView.builder()
                .id(testId)
                .trainingType(updatedTrainingType)
                .trainings(testTrainings)
                .trainers(testTrainers)
                .build();

        when(trainingViewRepository.findById(testId)).thenReturn(Optional.of(testTrainingView));
        when(trainingViewRepository.update(eq(testId), any(TrainingView.class))).thenReturn(updatedTrainingView);

        var result = trainingViewService.update(testId, updatedTrainingType);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(updatedTrainingType, result.trainingType());

        verify(trainingViewRepository).findById(testId);
        verify(trainingViewRepository).update(eq(testId), argThat(view ->
                view.getTrainingType() == updatedTrainingType));
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenTrainingViewNotFound() {
        when(trainingViewRepository.findById(testId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () ->
                trainingViewService.update(testId, updatedTrainingType));

        assertEquals("Training view not found", exception.getMessage());
        verify(trainingViewRepository).findById(testId);
        verify(trainingViewRepository, never()).update(anyString(), any());
    }

    @Test
    void delete_shouldDeleteTrainingView() throws NotFoundException {
        doNothing().when(trainingViewRepository).delete(testId);

        trainingViewService.delete(testId);

        verify(trainingViewRepository).delete(testId);
    }

    @Test
    void delete_shouldHandleNotFoundExceptionGracefully() throws NotFoundException {
        doThrow(new NotFoundException("Training view not found")).when(trainingViewRepository).delete(testId);

        var exception = assertThrows(NotFoundException.class, () ->
                trainingViewService.delete(testId));

        assertEquals("Training view not found", exception.getMessage());
        verify(trainingViewRepository).delete(testId);
    }

    @Test
    void findAll_shouldReturnAllTrainingViews() throws NotFoundException {
        var secondTrainingView = TrainingView.builder()
                .id("test-id-456")
                .trainingType(TrainingType.SELF_PLACING)
                .trainings(Collections.emptyList())
                .trainers(Collections.emptyList())
                .build();

        var trainingViews = List.of(testTrainingView, secondTrainingView);

        when(trainingViewRepository.findAll()).thenReturn(trainingViews);

        var results = trainingViewService.findAll();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(testId, results.get(0).id());
        assertEquals(testTrainingType, results.get(0).trainingType());
        assertEquals("test-id-456", results.get(1).id());
        assertEquals(TrainingType.SELF_PLACING, results.get(1).trainingType());

        verify(trainingViewRepository).findAll();
    }

    @Test
    void findAll_shouldHandleEmptyList() throws NotFoundException {
        when(trainingViewRepository.findAll()).thenReturn(Collections.emptyList());

        var results = trainingViewService.findAll();

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(trainingViewRepository).findAll();
    }

    @Test
    void findById_shouldReturnTrainingViewWhenFound() throws NotFoundException {
        when(trainingViewRepository.findById(testId)).thenReturn(Optional.of(testTrainingView));

        var result = trainingViewService.findById(testId);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testTrainingType, result.trainingType());

        var expectedTrainingIds = testTrainings.stream()
                .map(Training::getId)
                .collect(Collectors.toList());
        assertEquals(expectedTrainingIds, result.trainingsIds());

        var expectedSpecializationIds = testTrainers.stream()
                .map(Trainer::getId)
                .collect(Collectors.toList());
        assertEquals(expectedSpecializationIds, result.specializationIds());

        verify(trainingViewRepository).findById(testId);
    }

    @Test
    void findById_shouldThrowNotFoundExceptionWhenNotFound() {
        when(trainingViewRepository.findById(testId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () ->
                trainingViewService.findById(testId));

        assertEquals("Training view not found", exception.getMessage());
        verify(trainingViewRepository).findById(testId);
    }
}