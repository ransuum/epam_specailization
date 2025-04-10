package org.epam.service.impl;

import lombok.RequiredArgsConstructor;
import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TraineeDto;
import org.epam.models.entity.Trainee;
import org.epam.models.dto.create.TraineeCreateDto;
import org.epam.models.dto.update.TraineeRequestDto;
import org.epam.models.entity.User;
import org.epam.models.enums.NotFoundMessages;
import org.epam.repository.TraineeRepository;
import org.epam.security.config.SecurityService;
import org.epam.service.TraineeService;
import org.epam.utils.CredentialsGenerator;
import org.epam.utils.mappers.TraineeMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.epam.utils.FieldValidator.check;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final CredentialsGenerator credentialsGenerator;
    private final SecurityService securityService;

    @Override
    @Transactional
    public Trainee save(TraineeCreateDto traineeCreationData) throws NotFoundException {
        final var username = credentialsGenerator.generateUsername(traineeCreationData.firstname(), traineeCreationData.lastname());
        final var user = User.builder()
                .firstName(traineeCreationData.firstname())
                .lastName(traineeCreationData.lastname())
                .isActive(Boolean.TRUE)
                .roles("ROLE_TRAINEE")
                .username(username)
                .password(credentialsGenerator.generatePassword(username))
                .build();
        return traineeRepository.save(Trainee.builder()
                .address(traineeCreationData.address())
                .dateOfBirth(LocalDate.parse(traineeCreationData.dateOfBirth(), FORMATTER))
                .user(user)
                .build());
    }

    @Override
    @Transactional
    public TraineeDto update(TraineeRequestDto traineeUpdateData) throws NotFoundException {
        final var authUsername = securityService.getCurrentUserEmail();
        final var traineeById = traineeRepository.findByUser_Username(authUsername)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        if (check(traineeUpdateData.getAddress())) traineeById.setAddress(traineeUpdateData.getAddress());
        if (check(traineeUpdateData.getDateOfBirth()))
            traineeById.setDateOfBirth(LocalDate.parse(traineeUpdateData.getDateOfBirth(), FORMATTER));
        traineeById.getUser().setIsActive(traineeUpdateData.getIsActive());
        traineeById.getUser().setUsername(traineeUpdateData.getUsername());
        traineeById.getUser().setFirstName(traineeUpdateData.getFirstname());
        traineeById.getUser().setLastName(traineeUpdateData.getLastname());
        return TraineeMapper.INSTANCE.toDto(traineeRepository.save(traineeById));
    }

    @Override
    @Transactional
    public void delete(String id) {
        final var trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINEE.getVal()));
        traineeRepository.delete(trainee);
    }

    @Override
    public Page<TraineeDto> findAll(Pageable pageable) {
        return traineeRepository.findAll(pageable).map(TraineeMapper.INSTANCE::toDto);
    }

    @Override
    @Transactional
    public TraineeDto findById(String id) throws NotFoundException {
        return TraineeMapper.INSTANCE.toDto(traineeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with this credentials")));

    }

    @Override
    public TraineeDto profile() throws NotFoundException {
        final var authUsername = securityService.getCurrentUserEmail();
        return findByUsername(authUsername);
    }

    @Override
    @Transactional
    public TraineeDto changePassword(String oldPassword, String newPassword) throws NotFoundException, CredentialException {
        final var authUsername = securityService.getCurrentUserEmail();
        final var trainee = traineeRepository.findByUser_Username(authUsername)
                .orElseThrow(() -> new NotFoundException("Trainee not found with authUsername " + authUsername));

        if (!trainee.getUser().getPassword().equals(oldPassword))
            throw new CredentialException("Old password do not match");
        trainee.getUser().setPassword(newPassword);
        return TraineeMapper.INSTANCE.toDto(traineeRepository.save(trainee));
    }

    @Override
    public TraineeDto findByUsername(String username) throws NotFoundException {
        return TraineeMapper.INSTANCE.toDto(traineeRepository.findByUser_Username(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found by username")));
    }

    @Override
    @Transactional
    public String deleteByUsername(String username) throws NotFoundException {
        traineeRepository.deleteByUser_Username(username);
        return username;
    }

    @Override
    @Transactional
    public TraineeDto changeStatus() throws NotFoundException {
        final var authUsername = securityService.getCurrentUserEmail();
        final var trainee = traineeRepository.findByUser_Username(authUsername)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));

        trainee.getUser().setIsActive(trainee.getUser().getIsActive()
                .equals(Boolean.TRUE) ? Boolean.FALSE : Boolean.TRUE);
        return TraineeMapper.INSTANCE.toDto(traineeRepository.save(trainee));
    }
}
