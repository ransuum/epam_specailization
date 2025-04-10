package org.epam.service.impl;

import lombok.RequiredArgsConstructor;
import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainerDto;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.entity.User;
import org.epam.models.enums.NotFoundMessages;
import org.epam.models.dto.create.TrainerCreateDto;
import org.epam.models.dto.update.TrainerUpdateDto;
import org.epam.models.enums.TrainingTypeName;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainerRepository;
import org.epam.repository.TrainingTypeRepository;
import org.epam.security.config.SecurityService;
import org.epam.service.TrainerService;
import org.epam.utils.CredentialsGenerator;
import org.epam.utils.mappers.TrainerMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.epam.utils.FieldValidator.check;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TraineeRepository traineeRepository;
    private final CredentialsGenerator credentialsGenerator;
    private final SecurityService securityService;

    @Override
    @Transactional
    public Trainer save(TrainerCreateDto trainerCreateData) throws NotFoundException {
        final var username = credentialsGenerator.generateUsername(trainerCreateData.firstname(), trainerCreateData.lastname());
        final var user = User.builder()
                .firstName(trainerCreateData.firstname())
                .lastName(trainerCreateData.lastname())
                .username(username)
                .password(credentialsGenerator.generatePassword(username))
                .isActive(Boolean.TRUE)
                .build();
        return trainerRepository.save(
                Trainer.builder()
                        .user(user)
                        .specialization(trainingTypeRepository.findByTrainingTypeName(TrainingTypeName
                                        .getTrainingNameFromString(trainerCreateData.specialization()))
                                .orElseThrow(() -> new NotFoundException("Specialization Not Found")))
                        .build());
    }

    @Override
    @Transactional
    public TrainerDto update(TrainerUpdateDto trainerUpdateData) throws NotFoundException {
        var authUsername = securityService.getCurrentUserEmail();
        var trainer = trainerRepository.findByUser_Username(authUsername)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal()));

        if (check(trainerUpdateData.specialization()))
            trainer.setSpecialization(trainingTypeRepository.findByTrainingTypeName(
                            TrainingTypeName.getTrainingNameFromString(trainerUpdateData.specialization()))
                    .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINING_TYPE.getVal())));

        trainer.getUser().setIsActive(trainerUpdateData.isActive());
        trainer.getUser().setUsername(trainerUpdateData.username());
        trainer.getUser().setFirstName(trainerUpdateData.firstname());
        trainer.getUser().setLastName(trainerUpdateData.lastname());
        return TrainerMapper.INSTANCE.toDto(trainerRepository.save(trainer));
    }

    @Override
    @Transactional
    public void delete(String id) throws NotFoundException {
        var trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal()));
        trainerRepository.delete(trainer);
    }

    @Override
    public Page<TrainerDto> findAll(Pageable pageable) {
        return trainerRepository.findAll(pageable).map(TrainerMapper.INSTANCE::toDto);
    }

    @Override
    @Transactional
    public TrainerDto findById(String id) throws NotFoundException {
        return TrainerMapper.INSTANCE.toDto(trainerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with this credentials")));
    }

    @Override
    @Transactional
    public TrainerDto profile() throws NotFoundException {
        var authUsername = securityService.getCurrentUserEmail();
        return TrainerMapper.INSTANCE.toDto(trainerRepository.findByUser_Username(authUsername)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal())));
    }

    @Override
    @Transactional
    public TrainerDto changePassword(String oldPassword, String newPassword) throws NotFoundException, CredentialException {
        var authUsername = securityService.getCurrentUserEmail();
        var trainer = trainerRepository.findByUser_Username(authUsername)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal()));

        if (!trainer.getUser().getPassword().equals(oldPassword))
            throw new CredentialException("Old password do not match");

        trainer.getUser().setPassword(newPassword);
        return TrainerMapper.INSTANCE.toDto(trainerRepository.save(trainer));
    }

    @Override
    @Transactional
    public TrainerDto findByUsername(String username) throws NotFoundException {
        return TrainerMapper.INSTANCE.toDto(trainerRepository.findByUser_Username(username)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal())));

    }

    @Override
    @Transactional
    public TrainerDto changeStatus(String username) throws NotFoundException {
        var trainer = trainerRepository.findByUser_Username(username)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINER.getVal()));

        trainer.getUser().setIsActive(trainer.getUser().getIsActive()
                .equals(Boolean.TRUE) ? Boolean.FALSE : Boolean.TRUE);
        return TrainerMapper.INSTANCE.toDto(trainerRepository.save(trainer));
    }

    @Override
    public List<TrainerDto> getUnassignedTrainers(String username) throws NotFoundException {
        var trainee = traineeRepository.findByUser_Username(username)
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
