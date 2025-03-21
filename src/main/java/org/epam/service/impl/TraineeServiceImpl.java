package org.epam.service.impl;

import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TraineeDto;
import org.epam.models.dto.TrainingDto;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Training;
import org.epam.models.request.traineerequest.TraineeRequestCreate;
import org.epam.models.request.traineerequest.TraineeRequestUpdate;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainingRepository;
import org.epam.repository.UserRepository;
import org.epam.service.TraineeService;
import org.epam.utils.mappers.TraineeMapper;
import org.epam.utils.mappers.TrainingMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.utils.CheckerField.check;

@Service
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;

    public TraineeServiceImpl(UserRepository userRepository, TraineeRepository traineeRepository, TrainingRepository trainingRepository) {
        this.traineeRepository = traineeRepository;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
    }

    @Override
    public TraineeDto save(TraineeRequestCreate request) throws NotFoundException {
        return TraineeMapper.INSTANCE.toDto(traineeRepository.save(Trainee.builder()
                .address(request.address())
                .dateOfBirth(request.dateOfBirth())
                .user(userRepository.findById(request.userId())
                        .orElseThrow(() -> new NotFoundException("User not found")))
                .build()));
    }

    @Override
    public TraineeDto update(String id, TraineeRequestUpdate requestUpdate) throws NotFoundException {
        var traineeById = traineeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        if (check(requestUpdate.getAddress())) traineeById.setAddress(requestUpdate.getAddress());
        if (check(requestUpdate.getDateOfBirth())) traineeById.setDateOfBirth(requestUpdate.getDateOfBirth());
        if (check(requestUpdate.getUserId())) traineeById.setUser(userRepository.findById(requestUpdate.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found")));
        return TraineeMapper.INSTANCE.toDto(traineeRepository.update(id, traineeById));
    }

    @Override
    public void delete(String id) {
        traineeRepository.delete(id);
    }

    @Override
    public List<TraineeDto> findAll() {
        return traineeRepository.findAll()
                .stream()
                .map(TraineeMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public TraineeDto findById(String id) throws NotFoundException {
        return TraineeMapper.INSTANCE.toDto(traineeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with this credentials")));

    }

    @Override
    public TraineeDto changePassword(String id, String oldPassword, String newPassword) throws NotFoundException, CredentialException {
        var trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainee not found with id " + id));

        if (!trainee.getUser().getPassword().equals(oldPassword))
            throw new CredentialException("Old password do not match");
        var user = trainee.getUser();
        user.setPassword(newPassword);
        userRepository.update(user.getId(), user);
        return TraineeMapper.INSTANCE.toDto(trainee);
    }

    @Override
    public TraineeDto findByUsername(String username) throws NotFoundException {
        return TraineeMapper.INSTANCE.toDto(traineeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found by username")));
    }

    @Override
    public String deleteByUsername(String username) throws NotFoundException {
        return traineeRepository.deleteByUsername(username);
    }

    @Override
    public TraineeDto activateAction(String username) throws NotFoundException {
        var trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        var user = trainee.getUser();
        user.setIsActive(Boolean.TRUE);
        trainee.setUser(userRepository.update(user.getId(), user));
        return TraineeMapper.INSTANCE.toDto(trainee);
    }

    @Override
    public TraineeDto deactivateAction(String username) throws NotFoundException {
        var trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        var user = trainee.getUser();
        user.setIsActive(Boolean.FALSE);
        trainee.setUser(userRepository.update(user.getId(), user));
        return TraineeMapper.INSTANCE.toDto(trainee);
    }

    @Override
    public List<TrainingDto> addTrainingsToTrainee(String traineeId, List<String> trainingIds) throws NotFoundException {
        var trainee = traineeRepository.findById(traineeId)
                .orElseThrow(() -> new NotFoundException("Trainee not found with id " + traineeId));

        List<Training> trainingsList = trainingIds.stream()
                .map(id -> trainingRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Training not found with id " + id)))
                .toList();

        trainee.getTrainings().addAll(trainingsList);
        return traineeRepository.update(traineeId, trainee).getTrainings()
                .stream()
                .map(TrainingMapper.INSTANCE::toDto)
                .toList();
    }
}
