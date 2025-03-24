package org.epam.service.impl;

import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TraineeDto;
import org.epam.models.entity.Trainee;
import org.epam.models.request.create.TraineeRequestUpdate;
import org.epam.repository.TraineeRepository;
import org.epam.repository.UserRepository;
import org.epam.service.TraineeService;
import org.epam.utils.mappers.TraineeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.epam.utils.CheckerField.check;

@Service
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;

    public TraineeServiceImpl(UserRepository userRepository, TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TraineeDto save(TraineeRequestUpdate request) throws NotFoundException {
        return TraineeMapper.INSTANCE.toDto(traineeRepository.save(Trainee.builder()
                .address(request.address())
                .dateOfBirth(request.dateOfBirth())
                .user(userRepository.findById(request.userId())
                        .orElseThrow(() -> new NotFoundException("User not found")))
                .build()));
    }

    @Override
    public TraineeDto update(String id, org.epam.models.request.update.TraineeRequestUpdate requestUpdate) throws NotFoundException {
        var traineeById = traineeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        if (check(requestUpdate.getAddress())) traineeById.setAddress(requestUpdate.getAddress());
        if (check(requestUpdate.getDateOfBirth())) traineeById.setDateOfBirth(requestUpdate.getDateOfBirth());
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
    @Transactional
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
    public TraineeDto changeStatus(String username) throws NotFoundException {
        var trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        var user = trainee.getUser();
        user.setIsActive(user.getIsActive().equals(Boolean.TRUE)
                ? Boolean.FALSE : Boolean.TRUE);
        trainee.setUser(userRepository.update(user.getId(), user));
        return TraineeMapper.INSTANCE.toDto(trainee);
    }
}
