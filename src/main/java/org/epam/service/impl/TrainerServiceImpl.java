package org.epam.service.impl;


import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainerDto;
import org.epam.models.entity.Trainer;
import org.epam.models.request.trainerrequest.TrainerRequestCreate;
import org.epam.models.request.trainerrequest.TrainerRequestUpdate;
import org.epam.repository.TrainerRepository;
import org.epam.repository.UserRepository;
import org.epam.service.TrainerService;
import org.epam.utils.mappers.TrainerMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.utils.CheckerField.check;


@Service
public class TrainerServiceImpl implements TrainerService {
    private static final Logger log = LogManager.getLogger(TrainerServiceImpl.class);
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;

    public TrainerServiceImpl(TrainerRepository trainerRepository, UserRepository userRepository) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TrainerDto save(TrainerRequestCreate request) {
        try {
            return TrainerMapper.INSTANCE.toDto(trainerRepository.save(
                    Trainer.builder()
                            .user(userRepository.findById(request.userId())
                                    .orElseThrow(() -> new NotFoundException("User not found")))
                            .specialization(request.specialization())
                            .build())
            );
        } catch (NotFoundException e) {
            log.error("User not found with this id: {}", request.userId());
            return null;
        }
    }

    @Override
    public TrainerDto update(String id, TrainerRequestUpdate request) throws NotFoundException {
        try {
            var trainer = trainerRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Trainer not found"));

            if (check(request.specialization())) trainer.setSpecialization(request.specialization());
            if (check(request.userId())) trainer.setUser(userRepository.findById(request.userId())
                    .orElseThrow(() -> new NotFoundException("User not found")));
            return TrainerMapper.INSTANCE.toDto(trainerRepository.update(id, trainer));
        } catch (NotFoundException e) {
            log.error("Something went wrong with update: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            trainerRepository.delete(id);
        } catch (NotFoundException e) {
            log.error("Trainer not found with id with delete: {}", id);
        }
    }

    @Override
    public List<TrainerDto> findAll() {
        return trainerRepository.findAll()
                .stream()
                .map(TrainerMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public TrainerDto findById(String id) {
        try {
            return TrainerMapper.INSTANCE.toDto(trainerRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Trainer not found")));
        } catch (NotFoundException e) {
            log.error("Trainer not found with id: {}", id);
            return null;
        }
    }

    @Override
    public TrainerDto changePassword(String id, String oldPassword, String newPassword) {
        try {
            var trainer = trainerRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Trainer not found"));

            if (!trainer.getUser().getPassword().equals(oldPassword))
                throw new CredentialException("Old password do not match");

            var user = trainer.getUser();
            user.setPassword(newPassword);
            userRepository.update(user.getId(), user);
            return TrainerMapper.INSTANCE.toDto(trainer);
        } catch (NotFoundException | CredentialException e) {
            log.error("Something went wrong with change password: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public TrainerDto findByUsername(String username) {
        try {
            return TrainerMapper.INSTANCE.toDto(trainerRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException("Trainer not found")));
        } catch (NotFoundException e) {
            log.error("Erorr when trying to find trainer with username: {}", username);
            return null;
        }
    }

    @Override
    public TrainerDto activateAction(String username) {
        var trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found"));

        var user = trainer.getUser();
        user.setIsActive(Boolean.TRUE);
        trainer.setUser(userRepository.update(user.getId(), user));
        return TrainerMapper.INSTANCE.toDto(trainer);
    }

    @Override
    public TrainerDto deactivateAction(String username) {
        var trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found"));

        var user = trainer.getUser();
        user.setIsActive(Boolean.FALSE);
        trainer.setUser(userRepository.update(user.getId(), user));
        return TrainerMapper.INSTANCE.toDto(trainer);
    }
}
