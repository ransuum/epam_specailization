package org.epam.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TraineeDto;
import org.epam.models.entity.Trainee;
import org.epam.models.dto.create.TraineeCreateDto;
import org.epam.models.dto.update.TraineeRequestDto;
import org.epam.models.entity.User;
import org.epam.repository.TraineeRepository;
import org.epam.service.TraineeService;
import org.epam.utils.CredentialsGenerator;
import org.epam.utils.mappers.TraineeMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.epam.utils.FieldValidator.check;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final CredentialsGenerator credentialsGenerator;

    @Override
    @Transactional
    public AuthResponseDto save(TraineeCreateDto traineeCreationData) throws NotFoundException {
        var username = credentialsGenerator.generateUsername(traineeCreationData.firstname(), traineeCreationData.lastname());
        return TraineeMapper.INSTANCE.toAuthResponseDto(traineeRepository.save(Trainee.builder()
                .address(traineeCreationData.address())
                .dateOfBirth(LocalDate.parse(traineeCreationData.dateOfBirth(), FORMATTER))
                .user(User.builder()
                        .firstName(traineeCreationData.firstname())
                        .lastName(traineeCreationData.lastname())
                        .isActive(Boolean.TRUE)
                        .username(username)
                        .password(credentialsGenerator.generatePassword(username))
                        .build())
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
        trainee.getUser().setPassword(newPassword);
        return TraineeMapper.INSTANCE.toDto(traineeRepository.update(id, trainee));
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

        trainee.getUser().setIsActive(trainee.getUser().getIsActive()
                .equals(Boolean.TRUE) ? Boolean.FALSE : Boolean.TRUE);
        return TraineeMapper.INSTANCE.toDto(traineeRepository.update(trainee.getId(), trainee));
    }
}
