package org.epam.service;

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
    private TrainingRepo trainingRepo;

    @Mock
    private TrainerRepo trainerRepo;

    @Mock
    private TraineeRepo traineeRepo;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void testSaveTrainingSuccess() {
        TrainingRequest request = new TrainingRequest(1, 1, "Java Training", TrainingType.ONLINE, LocalDate.now(), 60);
        Trainee trainee = new Trainee(1, "123 Main St", LocalDate.of(1990, 1, 1), "John", "Doe", "john_doe", "password", true);
        Trainer trainer = new Trainer(1, "Java", "Jane", "Smith", "jane_smith", "password", true);

        when(traineeRepo.findById(request.traineeId())).thenReturn(Optional.of(trainee));
        when(trainerRepo.findById(request.trainerId())).thenReturn(Optional.of(trainer));

        Training trainingToSave = new Training(trainee, trainer, request.trainingName(),
                request.trainingType(), request.trainingDate(), request.trainingDuration());
        when(trainingRepo.save(any(Training.class))).thenReturn(trainingToSave);

        Training savedTraining = trainingService.save(request);

        assertNotNull(savedTraining);
        assertEquals(request.trainingName(), savedTraining.getTrainingName());
        verify(traineeRepo).findById(request.traineeId());
        verify(trainerRepo).findById(request.trainerId());
        verify(trainingRepo).save(any(Training.class));
    }

    @Test
    void testSaveTrainingTraineeNotFound() {
        TrainingRequest request = new TrainingRequest(1, 1, "Java Training", TrainingType.ONLINE, LocalDate.now(), 60);
        when(traineeRepo.findById(request.traineeId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> trainingService.save(request));
        assertEquals("Trainee not found", exception.getMessage());
    }

    @Test
    void testUpdateTrainingSuccess() {
        Trainee oldTrainee = new Trainee(1, "Old Address", LocalDate.of(1990, 1, 1),
                "John", "Doe", "john_doe", "password", true);
        Trainer oldTrainer = new Trainer(1, "Old Specialization", "Jane", "Smith", "jane_smith", "password", true);
        Training existingTraining = new Training(oldTrainee, oldTrainer, "Old Training",
                TrainingType.OFFLINE, LocalDate.now().minusDays(1), 45);
        int trainingId = existingTraining.getId();

        when(trainingRepo.findById(trainingId)).thenReturn(Optional.of(existingTraining));

        TrainingRequest updateRequest = new TrainingRequest(null, null, "Updated Training",
                TrainingType.ONLINE, LocalDate.now(), 90);

        when(trainingRepo.update(existingTraining)).thenReturn(existingTraining);

        Training updatedTraining = trainingService.update(trainingId, updateRequest);

        assertEquals("Updated Training", updatedTraining.getTrainingName());
        assertEquals(TrainingType.ONLINE, updatedTraining.getTrainingType());
        assertEquals(90, updatedTraining.getTrainingDuration());
        assertEquals(LocalDate.now(), updatedTraining.getTrainingDate());
    }

    @Test
    void testUpdateTrainingWithTraineeAndTrainerChange() {
        Trainee oldTrainee = new Trainee(1, "Old Address", LocalDate.of(1990, 1, 1),
                "John", "Doe", "john_doe", "password", true);
        Trainer oldTrainer = new Trainer(1, "Old Specialization", "Jane", "Smith", "jane_smith", "password", true);
        Training existingTraining = new Training(oldTrainee, oldTrainer, "Old Training",
                TrainingType.OFFLINE, LocalDate.now().minusDays(1), 45);
        int trainingId = existingTraining.getId();

        when(trainingRepo.findById(trainingId)).thenReturn(Optional.of(existingTraining));

        Trainee newTrainee = new Trainee(2, "New Address", LocalDate.of(1995, 5, 5),
                "Alice", "Wonder", "alice_wonder", "password", true);
        Trainer newTrainer = new Trainer(2, "New Specialization", "Bob", "Marley", "bob_marley", "password", true);

        TrainingRequest updateRequest = new TrainingRequest(newTrainee.getId(), newTrainer.getId(), "Updated Training",
                TrainingType.ONLINE, LocalDate.now(), 60);
        when(traineeRepo.findById(newTrainee.getId())).thenReturn(Optional.of(newTrainee));
        when(trainerRepo.findById(newTrainer.getId())).thenReturn(Optional.of(newTrainer));
        when(trainingRepo.update(existingTraining)).thenReturn(existingTraining);

        Training updatedTraining = trainingService.update(trainingId, updateRequest);

        assertEquals(newTrainee, updatedTraining.getTrainee());
        assertEquals(newTrainer, updatedTraining.getTrainer());
        assertEquals("Updated Training", updatedTraining.getTrainingName());
    }

    @Test
    void testUpdateTrainingNotFound() {
        int trainingId = 1;
        TrainingRequest updateRequest = new TrainingRequest(null, null, "Updated Training",
                TrainingType.ONLINE, LocalDate.now(), 60);
        when(trainingRepo.findById(trainingId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> trainingService.update(trainingId, updateRequest));
        assertEquals("Training not found", exception.getMessage());
    }

    @Test
    void testDeleteTrainingSuccess() {
        Trainee trainee = new Trainee(1, "Address", LocalDate.of(1990, 1, 1),
                "John", "Doe", "john_doe", "password", true);
        Trainer trainer = new Trainer(1, "Specialization", "Jane", "Smith", "jane_smith", "password", true);
        Training training = new Training(trainee, trainer, "Training", TrainingType.ONLINE, LocalDate.now(), 60);
        int trainingId = training.getId();

        when(trainingRepo.findById(trainingId)).thenReturn(Optional.of(training));
        doNothing().when(trainingRepo).delete(trainingId);

        trainingService.delete(trainingId);

        verify(trainingRepo, times(1)).delete(trainingId);
    }

    @Test
    void testFindAllTrainings() {
        // Arrange
        Trainee trainee = new Trainee(1, "Address", LocalDate.of(1990, 1, 1),
                "John", "Doe", "john_doe", "password", true);
        Trainer trainer = new Trainer(1, "Specialization", "Jane", "Smith", "jane_smith", "password", true);
        Training training1 = new Training(trainee, trainer, "Training1", TrainingType.ONLINE, LocalDate.now(), 60);
        Training training2 = new Training(trainee, trainer, "Training2", TrainingType.OFFLINE, LocalDate.now().plusDays(1), 90);
        List<Training> trainings = List.of(training1, training2);

        when(trainingRepo.findAll()).thenReturn(trainings);

        List<Training> result = trainingService.findAll();

        assertEquals(2, result.size());
        assertEquals(trainings, result);
    }

    @Test
    void testFindByIdSuccess() {
        Trainee trainee = new Trainee(1, "Address", LocalDate.of(1990, 1, 1),
                "John", "Doe", "john_doe", "password", true);
        Trainer trainer = new Trainer(1, "Specialization", "Jane", "Smith", "jane_smith", "password", true);
        Training training = new Training(trainee, trainer, "Training", TrainingType.ONLINE, LocalDate.now(), 60);
        int trainingId = training.getId();

        when(trainingRepo.findById(trainingId)).thenReturn(Optional.of(training));

        Training result = trainingService.findById(trainingId);

        assertEquals(training, result);
    }

    @Test
    void testFindByIdNotFound() {
        when(trainingRepo.findById(anyInt())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> trainingService.findById(1));
        assertEquals("Training not found", exception.getMessage());
    }
}