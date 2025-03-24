package org.epam.service.impl;

import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainerDto;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.request.createrequest.TrainerRequestCreate;
import org.epam.models.request.updaterequest.TrainerRequestUpdate;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainerRepository;
import org.epam.repository.TrainingTypeRepository;
import org.epam.repository.UserRepository;
import org.epam.service.TrainerService;
import org.epam.utils.mappers.TrainerMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.utils.CheckerField.check;

@Service
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TraineeRepository traineeRepository;

    public TrainerServiceImpl(TrainerRepository trainerRepository, UserRepository userRepository,
                              TrainingTypeRepository trainingTypeRepository, TraineeRepository traineeRepository) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.traineeRepository = traineeRepository;
    }

    @Override
    public TrainerDto save(TrainerRequestCreate request) throws NotFoundException {
        return TrainerMapper.INSTANCE.toDto(trainerRepository.save(
                Trainer.builder()
                        .user(userRepository.findById(request.userId())
                                .orElseThrow(() -> new NotFoundException("User not found")))
                        .specialization(trainingTypeRepository.findById(request.specializationId())
                                .orElseThrow(() -> new NotFoundException("Specialization Not Found")))
                        .build())
        );
    }

    @Override
    public TrainerDto update(String id, TrainerRequestUpdate request) throws NotFoundException {
        var trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainer not found"));

        if (check(request.userId())) trainer.setUser(userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found")));

        if (check(request.specializationId()))
            trainer.setSpecialization(trainingTypeRepository.findById(request.specializationId())
                    .orElseThrow(() -> new NotFoundException("Specialization Not Found")));

        return TrainerMapper.INSTANCE.toDto(trainerRepository.update(id, trainer));
    }

    @Override
    public void delete(String id) throws NotFoundException {
        trainerRepository.delete(id);

    }

    @Override
    public List<TrainerDto> findAll() {
        return trainerRepository.findAll()
                .stream()
                .map(TrainerMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public TrainerDto findById(String id) throws NotFoundException {
        return TrainerMapper.INSTANCE.toDto(trainerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with this credentials")));
    }

    @Override
    public TrainerDto changePassword(String id, String oldPassword, String newPassword) throws NotFoundException, CredentialException {
        var trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainer not found"));

        if (!trainer.getUser().getPassword().equals(oldPassword))
            throw new CredentialException("Old password do not match");

        var user = trainer.getUser();
        user.setPassword(newPassword);
        userRepository.update(user.getId(), user);
        return TrainerMapper.INSTANCE.toDto(trainer);
    }

    @Override
    public TrainerDto findByUsername(String username) throws NotFoundException {
        return TrainerMapper.INSTANCE.toDto(trainerRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found")));

    }

    @Override
    public TrainerDto activateAction(String username) throws NotFoundException {
        var trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found"));

        var user = trainer.getUser();
        user.setIsActive(Boolean.TRUE);
        trainer.setUser(userRepository.update(user.getId(), user));
        return TrainerMapper.INSTANCE.toDto(trainer);
    }

    @Override
    public TrainerDto deactivateAction(String username) throws NotFoundException {
        var trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found"));

        var user = trainer.getUser();
        user.setIsActive(Boolean.FALSE);
        trainer.setUser(userRepository.update(user.getId(), user));
        return TrainerMapper.INSTANCE.toDto(trainer);
    }

    @Override
    public List<TrainerDto> getUnassignedTrainersForTrainee(String username) throws NotFoundException {
        var trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found with username: " + username));

        var assignedTrainers = trainee.getTrainings().stream()
                .map(Training::getTrainer)
                .distinct()
                .toList();

        return trainerRepository.findAll()
                .stream()
                .filter(trainer -> !assignedTrainers.contains(trainer))
                .map(TrainerMapper.INSTANCE::toDto)
                .toList();
    }
}
