package org.epam.service;

import org.epam.exception.NotFoundException;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingViewServiceTest {
    private static final String TEST_ID = "test-id";

    @Mock
    private TrainingViewRepository trainingViewRepository;

    @InjectMocks
    private TrainingViewServiceImpl trainingViewService;

    private TrainingView testTrainingView;

    @BeforeEach
    void setUp() {
        testTrainingView = TrainingView.builder()
                .id(TEST_ID)
                .trainingType(TrainingType.LABORATORY)
                .trainings(new ArrayList<>())
                .build();
    }

    @Test
    void save_shouldCreateNewTrainingView() {
        when(trainingViewRepository.save(any(TrainingView.class))).thenReturn(testTrainingView);

        var result = trainingViewService.save(TrainingType.LABORATORY);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TrainingType.LABORATORY, result.trainingType());
        assertTrue(result.trainingsIds().isEmpty());

        verify(trainingViewRepository).save(any(TrainingView.class));
    }

    @Test
    void update_shouldUpdateTrainingType() {
        var updatedTrainingView = TrainingView.builder()
                .id(TEST_ID)
                .trainingType(TrainingType.FUNDAMENTALS)
                .trainings(new ArrayList<>())
                .build();

        when(trainingViewRepository.findById(TEST_ID)).thenReturn(Optional.of(testTrainingView));
        when(trainingViewRepository.update(eq(TEST_ID), any(TrainingView.class))).thenReturn(updatedTrainingView);

        var result = trainingViewService.update(TEST_ID, TrainingType.FUNDAMENTALS);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TrainingType.FUNDAMENTALS, result.trainingType());

        verify(trainingViewRepository).findById(TEST_ID);
        verify(trainingViewRepository).update(eq(TEST_ID), any(TrainingView.class));
    }

    @Test
    void update_shouldReturnNullWhenTrainingViewNotFound() {
        when(trainingViewRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        var result = trainingViewService.update(TEST_ID, TrainingType.SELF_PLACING);

        assertNull(result);
        verify(trainingViewRepository).findById(TEST_ID);
        verify(trainingViewRepository, never()).update(any(), any());
    }

    @Test
    void delete_shouldDeleteTrainingView() {
        doNothing().when(trainingViewRepository).delete(TEST_ID);

        trainingViewService.delete(TEST_ID);

        verify(trainingViewRepository).delete(TEST_ID);
    }

    @Test
    void delete_shouldHandleNotFoundExceptionGracefully() {
        doThrow(new NotFoundException("Training view not found")).when(trainingViewRepository).delete(TEST_ID);

        assertDoesNotThrow(() -> trainingViewService.delete(TEST_ID));
        verify(trainingViewRepository).delete(TEST_ID);
    }

    @Test
    void findAll_shouldReturnAllTrainingViews() {
        var secondTrainingView = TrainingView.builder()
                .id("second-id")
                .trainingType(TrainingType.SELF_PLACING)
                .trainings(new ArrayList<>())
                .build();

        when(trainingViewRepository.findAll()).thenReturn(Arrays.asList(testTrainingView, secondTrainingView));

        var result = trainingViewService.findAll();

        assertEquals(2, result.size());
        assertEquals(TEST_ID, result.get(0).id());
        assertEquals("second-id", result.get(1).id());
        assertEquals(TrainingType.LABORATORY, result.get(0).trainingType());
        assertEquals(TrainingType.SELF_PLACING, result.get(1).trainingType());

        verify(trainingViewRepository).findAll();
    }

    @Test
    void findById_shouldReturnTrainingViewWhenFound() {
        when(trainingViewRepository.findById(TEST_ID)).thenReturn(Optional.of(testTrainingView));

        var result = trainingViewService.findById(TEST_ID);

        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TrainingType.LABORATORY, result.trainingType());

        verify(trainingViewRepository).findById(TEST_ID);
    }

    @Test
    void findById_shouldReturnNullWhenNotFound() {
        when(trainingViewRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        var result = trainingViewService.findById(TEST_ID);

        assertNull(result);
        verify(trainingViewRepository).findById(TEST_ID);
    }
}