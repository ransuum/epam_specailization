package org.epam.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.epam.models.dto.TrainingDto;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingType;
import org.epam.models.request.TrainingRequest;
import org.epam.repository.TraineeRepo;
import org.epam.repository.TrainerRepo;
import org.epam.repository.TrainingRepo;
import org.epam.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TrainingRepo trainingRepo;

    @Mock
    private TrainerRepo trainerRepo;

    @Mock
    private TraineeRepo traineeRepo;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void testSaveTrainingSuccess() {
        Trainee trainee = new Trainee("123 Main St", LocalDate.of(1990, 1, 1),
                "John", "Doe", "jdoe", "pass", true);
        Trainer trainer = new Trainer("Java", "Alice", "Smith", "asmith", "pass", true);
        TrainingRequest request = new TrainingRequest(
                trainee.getId(), trainer.getId(), "Java Basics", TrainingType.ONLINE,
                LocalDate.of(2025, 3, 15), 60);

        when(traineeRepo.findById(request.traineeId())).thenReturn(Optional.of(trainee));
        when(trainerRepo.findById(request.trainerId())).thenReturn(Optional.of(trainer));

        Training savedTraining = new Training(trainee, trainer,
                request.trainingName(), request.trainingType(), request.trainingDate(), request.trainingDuration());
        when(trainingRepo.save(any(Training.class))).thenReturn(savedTraining);

        TrainingDto dummyDto = mock(TrainingDto.class);
        when(objectMapper.convertValue(savedTraining, TrainingDto.class)).thenReturn(dummyDto);

        TrainingDto result = trainingService.save(request);

        assertEquals(dummyDto, result);
        verify(traineeRepo).findById(request.traineeId());
        verify(trainerRepo).findById(request.trainerId());
        verify(trainingRepo).save(any(Training.class));
        verify(objectMapper).convertValue(savedTraining, TrainingDto.class);
    }

    @Test
    void testSaveTrainingTraineeNotFound() {
        Trainee trainee = new Trainee("123 Main St", LocalDate.of(1990, 1, 1),
                "John", "Doe", "jdoe", "pass", true);
        Trainer trainer = new Trainer("Java", "Alice", "Smith", "asmith", "pass", true);
        TrainingRequest request = new TrainingRequest(
                trainee.getId(), trainer.getId(), "Java Basics", TrainingType.ONLINE,
                LocalDate.of(2025, 3, 15), 60);

        when(traineeRepo.findById(request.traineeId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> trainingService.save(request));
        assertEquals("Trainee not found", exception.getMessage());
        verify(traineeRepo).findById(request.traineeId());
        verify(trainerRepo, never()).findById(any());
        verify(trainingRepo, never()).save(any());
    }

    @Test
    void testUpdateTrainingSuccess() {
        Trainee trainee = new Trainee("123 Main St", LocalDate.of(1990, 1, 1),
                "John", "Doe", "jdoe", "pass", true);
        Trainer trainer = new Trainer("Java", "Alice", "Smith", "asmith", "pass", true);
        Training existingTraining = new Training(trainee, trainer, "Java Basics",
                TrainingType.ONLINE, LocalDate.of(2025, 3, 15), 60);
        existingTraining.setId(1);

        TrainingRequest updateRequest = new TrainingRequest(
                trainee.getId(), trainer.getId(), "Advanced Java", TrainingType.ONLINE,
                LocalDate.of(2025, 4, 15), 90);
        when(trainingRepo.findById(1)).thenReturn(Optional.of(existingTraining));
        when(traineeRepo.findById(updateRequest.traineeId())).thenReturn(Optional.of(trainee));
        when(trainerRepo.findById(updateRequest.trainerId())).thenReturn(Optional.of(trainer));
        when(trainingRepo.update(existingTraining)).thenReturn(existingTraining);
        TrainingDto dummyDto = mock(TrainingDto.class);
        when(objectMapper.convertValue(existingTraining, TrainingDto.class)).thenReturn(dummyDto);

        TrainingDto result = trainingService.update(1, updateRequest);

        assertEquals("Advanced Java", existingTraining.getTrainingName());
        assertEquals(TrainingType.ONLINE, existingTraining.getTrainingType());
        assertEquals(LocalDate.of(2025, 4, 15), existingTraining.getTrainingDate());
        assertEquals(90, existingTraining.getTrainingDuration());
        assertEquals(trainee, existingTraining.getTrainee());
        assertEquals(trainer, existingTraining.getTrainer());
        assertEquals(dummyDto, result);

        verify(trainingRepo).findById(1);
        verify(traineeRepo).findById(updateRequest.traineeId());
        verify(trainerRepo).findById(updateRequest.trainerId());
        verify(trainingRepo).update(existingTraining);
        verify(objectMapper).convertValue(existingTraining, TrainingDto.class);
    }

    @Test
    void testUpdateTrainingWithTraineeAndTrainerChange() {
        Trainee oldTrainee = new Trainee("123 Main St", LocalDate.of(1990, 1, 1),
                "John", "Doe", "jdoe", "pass", true);
        Trainer oldTrainer = new Trainer("Java", "Alice", "Smith", "asmith", "pass", true);
        Training existingTraining = new Training(oldTrainee, oldTrainer, "Java Basics",
                TrainingType.ONLINE, LocalDate.of(2025, 3, 15), 60);
        existingTraining.setId(1);

        Trainee newTrainee = new Trainee("456 Other St", LocalDate.of(1991, 2, 2),
                "Jane", "Doe", "janedoe", "newpass", true);
        Trainer newTrainer = new Trainer("Python", "Bob", "Brown", "bobbrown", "newpass", true);

        TrainingRequest updateRequest = new TrainingRequest(
                newTrainee.getId(), newTrainer.getId(), "Advanced Java", TrainingType.OFFLINE,
                LocalDate.of(2025, 4, 15), 90);
        when(trainingRepo.findById(1)).thenReturn(Optional.of(existingTraining));
        when(traineeRepo.findById(updateRequest.traineeId())).thenReturn(Optional.of(newTrainee));
        when(trainerRepo.findById(updateRequest.trainerId())).thenReturn(Optional.of(newTrainer));
        when(trainingRepo.update(existingTraining)).thenReturn(existingTraining);
        TrainingDto dummyDto = mock(TrainingDto.class);
        when(objectMapper.convertValue(existingTraining, TrainingDto.class)).thenReturn(dummyDto);

        TrainingDto result = trainingService.update(1, updateRequest);

        assertEquals("Advanced Java", existingTraining.getTrainingName());
        assertEquals(TrainingType.OFFLINE, existingTraining.getTrainingType());
        assertEquals(LocalDate.of(2025, 4, 15), existingTraining.getTrainingDate());
        assertEquals(90, existingTraining.getTrainingDuration());
        assertEquals(newTrainee, existingTraining.getTrainee());
        assertEquals(newTrainer, existingTraining.getTrainer());
        assertEquals(dummyDto, result);

        verify(trainingRepo).findById(1);
        verify(traineeRepo).findById(updateRequest.traineeId());
        verify(trainerRepo).findById(updateRequest.trainerId());
        verify(trainingRepo).update(existingTraining);
        verify(objectMapper).convertValue(existingTraining, TrainingDto.class);
    }

    @Test
    void testUpdateTrainingNotFound() {
        TrainingRequest updateRequest = new TrainingRequest(
                1, 1, "Advanced Java", TrainingType.OFFLINE, LocalDate.of(2025, 4, 15), 90);
        when(trainingRepo.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> trainingService.update(1, updateRequest));
        assertEquals("Training not found", exception.getMessage());
        verify(trainingRepo).findById(1);
        verify(traineeRepo, never()).findById(any());
        verify(trainerRepo, never()).findById(any());
        verify(trainingRepo, never()).update(any());
    }

    @Test
    void testDeleteTrainingSuccess() {
        Trainee trainee = new Trainee("123 Main St", LocalDate.of(1990, 1, 1),
                "John", "Doe", "jdoe", "pass", true);
        Trainer trainer = new Trainer("Java", "Alice", "Smith", "asmith", "pass", true);
        Training existingTraining = new Training(trainee, trainer, "Java Basics",
                TrainingType.ONLINE, LocalDate.of(2025, 3, 15), 60);
        existingTraining.setId(1);

        TrainingDto dummyDto = mock(TrainingDto.class);
        when(trainingRepo.findById(1)).thenReturn(Optional.of(existingTraining));
        when(objectMapper.convertValue(existingTraining, TrainingDto.class)).thenReturn(dummyDto);
        when(dummyDto.id()).thenReturn(1);

        trainingService.delete(1);

        verify(trainingRepo).delete(1);
    }

    @Test
    void testFindAllTrainings() {
        Trainee trainee1 = new Trainee("123 Main St", LocalDate.of(1990, 1, 1),
                "John", "Doe", "jdoe", "pass", true);
        Trainer trainer1 = new Trainer("Java", "Alice", "Smith", "asmith", "pass", true);
        Training training1 = new Training(trainee1, trainer1, "Java Basics",
                TrainingType.ONLINE, LocalDate.of(2025, 3, 15), 60);
        training1.setId(1);

        Trainee trainee2 = new Trainee("456 Other St", LocalDate.of(1991, 2, 2),
                "Jane", "Doe", "janedoe", "pass", true);
        Trainer trainer2 = new Trainer("Python", "Bob", "Brown", "bobbrown", "pass", true);
        Training training2 = new Training(trainee2, trainer2, "Python Basics",
                TrainingType.ONLINE, LocalDate.of(2025, 4, 15), 90);
        training2.setId(2);

        List<Training> trainingList = List.of(training1, training2);
        when(trainingRepo.findAll()).thenReturn(trainingList);

        TrainingDto dto1 = mock(TrainingDto.class);
        TrainingDto dto2 = mock(TrainingDto.class);
        when(objectMapper.convertValue(training1, TrainingDto.class)).thenReturn(dto1);
        when(objectMapper.convertValue(training2, TrainingDto.class)).thenReturn(dto2);

        List<TrainingDto> result = trainingService.findAll();
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(dto1, dto2)));
        verify(trainingRepo).findAll();
        verify(objectMapper).convertValue(training1, TrainingDto.class);
        verify(objectMapper).convertValue(training2, TrainingDto.class);
    }

    @Test
    void testFindByIdSuccess() {
        Trainee trainee = new Trainee("123 Main St", LocalDate.of(1990, 1, 1),
                "John", "Doe", "jdoe", "pass", true);
        Trainer trainer = new Trainer("Java", "Alice", "Smith", "asmith", "pass", true);
        Training training = new Training(trainee, trainer, "Java Basics",
                TrainingType.OFFLINE, LocalDate.of(2025, 3, 15), 60);
        training.setId(1);
        TrainingDto dummyDto = mock(TrainingDto.class);
        when(trainingRepo.findById(1)).thenReturn(Optional.of(training));
        when(objectMapper.convertValue(training, TrainingDto.class)).thenReturn(dummyDto);

        TrainingDto result = trainingService.findById(1);
        assertEquals(dummyDto, result);
        verify(trainingRepo).findById(1);
        verify(objectMapper).convertValue(training, TrainingDto.class);
    }

    @Test
    void testFindByIdNotFound() {
        when(trainingRepo.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> trainingService.findById(1));
        assertEquals("Training not found", exception.getMessage());
        verify(trainingRepo).findById(1);
        verify(objectMapper, never()).convertValue(any(), eq(TrainingDto.class));
    }
}