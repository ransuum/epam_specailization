package org.epam.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TrainerDto;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.enums.NotFoundMessages;
import org.epam.models.request.create.TrainerRequestCreate;
import org.epam.models.request.update.TrainerUpdateDto;
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
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TraineeRepository traineeRepository;

    @Override
    @Transactional
    public AuthResponseDto save(TrainerRequestCreate trainerCreateData) throws NotFoundException {
        return TrainerMapper.INSTANCE.toAuthResponseDto(trainerRepository.save(
                Trainer.builder()
                        .user(userRepository.findById(trainerCreateData.userId())
                                .orElseThrow(() -> new NotFoundException("User not found")))
                        .specialization(trainingTypeRepository.findById(trainerCreateData.specializationId())
                                .orElseThrow(() -> new NotFoundException("Specialization Not Found")))
                        .build())
        );
    }

    @Override
    @Transactional
    public TrainerDto update(String id, TrainerUpdateDto trainerUpdateData) throws NotFoundException {
        var trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal()));

        if (check(trainerUpdateData.specializationId()))
            trainer.setSpecialization(trainingTypeRepository.findById(trainerUpdateData.specializationId())
                    .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINING_TYPE.getVal())));

        trainer.getUser().setIsActive(trainerUpdateData.isActive());
        trainer.getUser().setUsername(trainerUpdateData.username());
        trainer.getUser().setFirstName(trainerUpdateData.firstname());
        trainer.getUser().setLastName(trainerUpdateData.lastname());
        return TrainerMapper.INSTANCE.toDto(trainerRepository.update(id, trainer));
    }

    @Override
    @Transactional
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
    @Transactional
    public TrainerDto findById(String id) throws NotFoundException {
        return TrainerMapper.INSTANCE.toDto(trainerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with this credentials")));
    }

    @Override
    @Transactional
    public TrainerDto changePassword(String id, String oldPassword, String newPassword) throws NotFoundException, CredentialException {
        var trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal()));

        if (!trainer.getUser().getPassword().equals(oldPassword))
            throw new CredentialException("Old password do not match");

        var user = trainer.getUser();
        user.setPassword(newPassword);
        userRepository.update(user.getId(), user);
        return TrainerMapper.INSTANCE.toDto(trainer);
    }

    @Override
    @Transactional
    public TrainerDto findByUsername(String username) throws NotFoundException {
        return TrainerMapper.INSTANCE.toDto(trainerRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal())));

    }

    @Override
    @Transactional
    public TrainerDto changeStatus(String username) throws NotFoundException {
        var trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal()));

        var user = trainer.getUser();
        user.setIsActive(user.getIsActive().equals(Boolean.TRUE)
                ? Boolean.FALSE : Boolean.TRUE);
        trainer.setUser(userRepository.update(user.getId(), user));
        return TrainerMapper.INSTANCE.toDto(trainer);
    }

    @Override
    public List<TrainerDto> getUnassignedTrainers(String username) throws NotFoundException {
        var trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal()));

        var assignedTrainers = trainee.getTrainings().stream()
                .map(Training::getTrainer)
                .distinct()
                .toList();

        return trainerRepository.findAll()
                .stream()
                .filter(trainer -> !assignedTrainers.contains(trainer)
                        && trainer.getUser().getIsActive() == Boolean.TRUE)
                .map(TrainerMapper.INSTANCE::toDto)
                .toList();
    }
}
