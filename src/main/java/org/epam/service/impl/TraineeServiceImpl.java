package org.epam.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TraineeDto;
import org.epam.models.entity.Trainee;
import org.epam.models.request.create.TraineeRequestCreate;
import org.epam.models.request.update.TraineeRequestDto;
import org.epam.repository.TraineeRepository;
import org.epam.repository.UserRepository;
import org.epam.service.TraineeService;
import org.epam.utils.mappers.TraineeMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.epam.utils.CheckerField.check;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    @Transactional
    public AuthResponseDto save(TraineeRequestCreate traineeCreationData) throws NotFoundException {
        return TraineeMapper.INSTANCE.toAuthResponseDto(traineeRepository.save(Trainee.builder()
                .address(traineeCreationData.address())
                .dateOfBirth(traineeCreationData.dateOfBirth())
                .user(userRepository.findById(traineeCreationData.userId())
                        .orElseThrow(() -> new NotFoundException("User not found")))
                .build()));
    }

    @Override
    @Transactional
    public TraineeDto update(String id, TraineeRequestDto traineeUpdateData) throws NotFoundException {
        var traineeById = traineeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        if (check(traineeUpdateData.getAddress())) traineeById.setAddress(traineeUpdateData.getAddress());
        if (check(traineeUpdateData.getDateOfBirth()))
            traineeById.setDateOfBirth(LocalDate.parse(traineeUpdateData.getDateOfBirth(), FORMATTER));
        traineeById.getUser().setIsActive(traineeUpdateData.getIsActive());
        traineeById.getUser().setUsername(traineeUpdateData.getUsername());
        traineeById.getUser().setFirstName(traineeUpdateData.getFirstname());
        traineeById.getUser().setLastName(traineeUpdateData.getLastname());
        return TraineeMapper.INSTANCE.toDto(traineeRepository.update(id, traineeById));
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
    public TraineeDto findByUsername(String username) throws NotFoundException {
        return TraineeMapper.INSTANCE.toDto(traineeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found by username")));
    }

    @Override
    @Transactional
    public String deleteByUsername(String username) throws NotFoundException {
        return traineeRepository.deleteByUsername(username);
    }

    @Override
    @Transactional
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
