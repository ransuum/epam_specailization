package org.epam.service.impl;

import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TraineeDto;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.User;
import org.epam.models.request.traineeRequest.TraineeRequestCreate;
import org.epam.models.request.traineeRequest.TraineeRequestUpdate;
import org.epam.repository.TraineeRepository;
import org.epam.repository.UserRepository;
import org.epam.repository.inmemoryrepository.InMemoryTraineeRepository;
import org.epam.repository.inmemoryrepository.InMemoryUserRepository;
import org.epam.service.TraineeService;
import org.epam.utils.mappers.TraineeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.utils.CheckerField.check;

@Service
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;
    private static final Logger log = LogManager.getLogger(TraineeServiceImpl.class);
    private final UserRepository userRepository;

    public TraineeServiceImpl(UserRepository userRepository, TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TraineeDto save(TraineeRequestCreate request) {
        try {
            return TraineeMapper.INSTANCE.toDto(traineeRepository.save(Trainee.builder()
                    .address(request.address())
                    .dateOfBirth(request.dateOfBirth())
                    .user(userRepository.findById(request.userId())
                            .orElseThrow(() -> new NotFoundException("User not found")))
                    .build()));
        } catch (NotFoundException e) {
            log.error("User not found with id: {}", request.userId());
            return null;
        }
    }

    @Override
    public TraineeDto update(String id, TraineeRequestUpdate requestUpdate) {
        try {
            var traineeById = traineeRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Trainee not found"));

            if (check(requestUpdate.getAddress())) traineeById.setAddress(requestUpdate.getAddress());
            if (check(requestUpdate.getDateOfBirth())) traineeById.setDateOfBirth(requestUpdate.getDateOfBirth());
            if (check(requestUpdate.getUserId())) traineeById.setUser(userRepository.findById(requestUpdate.getUserId())
                    .orElseThrow(() -> new NotFoundException("User not found")));
            return TraineeMapper.INSTANCE.toDto(traineeRepository.update(id, traineeById));
        } catch (NotFoundException e) {
            log.error("Something wrong with finding entity: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            traineeRepository.delete(id);
        } catch (NotFoundException e) {
            log.error("Cannot delete Trainee by this id: {}", id);
        }
    }

    @Override
    public List<TraineeDto> findAll() {
        return traineeRepository.findAll()
                .stream()
                .map(TraineeMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public TraineeDto findById(String id) {
        try {
            return TraineeMapper.INSTANCE.toDto(traineeRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Trainee not found with id " + id)));
        } catch (NotFoundException e) {
            log.error("Trainee not found with id: {}", id);
            return null;
        }
    }

    @Override
    public TraineeDto changePassword(String id, String oldPassword, String newPassword) {
        try {
            var trainee = traineeRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Trainee not found with id " + id));

            if (!trainee.getUser().getPassword().equals(oldPassword))
                throw new CredentialException("Old password do not match");
            User user = trainee.getUser();
            user.setPassword(newPassword);
            userRepository.update(user.getId(), user);
            return TraineeMapper.INSTANCE.toDto(trainee);
        } catch (NotFoundException | CredentialException e) {
            log.error("Something went wrong: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public TraineeDto findByUsername(String username) {
        try {
            return TraineeMapper.INSTANCE.toDto(traineeRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException("Trainee not found by username")));
        } catch (NotFoundException e) {
            log.error("Trainee not found with username: {}", username);
            return null;
        }
    }

    @Override
    public String deleteByUsername(String username) {
        return traineeRepository.deleteByUsername(username);
    }

    @Override
    public TraineeDto activateAction(String username) {
        var trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        var user = trainee.getUser();
        user.setIsActive(Boolean.TRUE);
        trainee.setUser(userRepository.update(user.getId(), user));
        return TraineeMapper.INSTANCE.toDto(trainee);
    }

    @Override
    public TraineeDto deactivateAction(String username) {
        var trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        var user = trainee.getUser();
        user.setIsActive(Boolean.FALSE);
        trainee.setUser(userRepository.update(user.getId(), user));
        return TraineeMapper.INSTANCE.toDto(trainee);
    }
}
