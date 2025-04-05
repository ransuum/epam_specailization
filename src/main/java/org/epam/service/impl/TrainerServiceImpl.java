package org.epam.service.impl;

import lombok.RequiredArgsConstructor;
import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TrainerDto;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.entity.Users;
import org.epam.models.enums.NotFoundMessages;
import org.epam.models.dto.create.TrainerCreateDto;
import org.epam.models.dto.update.TrainerUpdateDto;
import org.epam.models.enums.TrainingTypeName;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainerRepository;
import org.epam.repository.TrainingTypeRepository;
import org.epam.service.TrainerService;
import org.epam.utils.CredentialsGenerator;
import org.epam.utils.mappers.TrainerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.epam.utils.CheckerBuilder.check;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TraineeRepository traineeRepository;
    private final CredentialsGenerator credentialsGenerator;

    @Override
    @Transactional
    public AuthResponseDto save(TrainerCreateDto trainerCreateData) throws NotFoundException {
        var username = credentialsGenerator.generateUsername(trainerCreateData.firstname(), trainerCreateData.lastname());
        return TrainerMapper.INSTANCE.toAuthResponseDto(trainerRepository.save(
                Trainer.builder()
                        .users(Users.builder()
                                .firstName(trainerCreateData.firstname())
                                .lastName(trainerCreateData.lastname())
                                .username(username)
                                .password(credentialsGenerator.generatePassword(username))
                                .isActive(Boolean.TRUE)
                                .build())
                        .specialization(trainingTypeRepository.findByTrainingTypeName(TrainingTypeName
                                        .getTrainingNameFromString(trainerCreateData.specialization()))
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

        trainer.getUsers().setIsActive(trainerUpdateData.isActive());
        trainer.getUsers().setUsername(trainerUpdateData.username());
        trainer.getUsers().setFirstName(trainerUpdateData.firstname());
        trainer.getUsers().setLastName(trainerUpdateData.lastname());
        return TrainerMapper.INSTANCE.toDto(trainerRepository.save(trainer));
    }

    @Override
    @Transactional
    public void delete(String id) throws NotFoundException {
        trainerRepository.deleteById(id);
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

        if (!trainer.getUsers().getPassword().equals(oldPassword))
            throw new CredentialException("Old password do not match");

        trainer.getUsers().setPassword(newPassword);
        return TrainerMapper.INSTANCE.toDto(trainerRepository.save(trainer));
    }

    @Override
    @Transactional
    public TrainerDto findByUsername(String username) throws NotFoundException {
        return TrainerMapper.INSTANCE.toDto(trainerRepository.findByUsers_Username(username)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal())));

    }

    @Override
    @Transactional
    public TrainerDto changeStatus(String username) throws NotFoundException {
        var trainer = trainerRepository.findByUsers_Username(username)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal()));

        trainer.getUsers().setIsActive(trainer.getUsers().getIsActive()
                .equals(Boolean.TRUE) ? Boolean.FALSE : Boolean.TRUE);
        return TrainerMapper.INSTANCE.toDto(trainerRepository.save(trainer));
    }

    @Override
    public List<TrainerDto> getUnassignedTrainers(String username) throws NotFoundException {
        var trainee = traineeRepository.findByUsers_Username(username)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal()));

        var assignedTrainers = trainee.getTrainings().stream()
                .map(Training::getTrainer)
                .distinct()
                .toList();

        return trainerRepository.findAll()
                .stream()
                .filter(trainer -> !assignedTrainers.contains(trainer)
                        && trainer.getUsers().getIsActive() == Boolean.TRUE)
                .map(TrainerMapper.INSTANCE::toDto)
                .toList();
    }
}
