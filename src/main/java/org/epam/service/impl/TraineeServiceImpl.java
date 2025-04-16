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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Trainee save(TraineeCreateDto traineeCreationData) throws NotFoundException {
        final var username = credentialsGenerator.generateUsername(traineeCreationData.firstname(), traineeCreationData.lastname());
        final var rawPassword = credentialsGenerator.generatePassword(username);
        final var hashedPassword = passwordEncoder.encode(rawPassword);

        final var user = User.builder()
                .firstName(traineeCreationData.firstname())
                .lastName(traineeCreationData.lastname())
                .isActive(Boolean.TRUE)
                .roles("ROLE_TRAINEE")
                .username(username)
                .password(hashedPassword)
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
        final var authUsername = securityService.getCurrentUsername();
        return traineeRepository.findByUser_Username(authUsername)
                .map(trainee -> {
                    if (check(traineeUpdateData.getAddress())) trainee.setAddress(traineeUpdateData.getAddress());
                    if (check(traineeUpdateData.getDateOfBirth()))
                        trainee.setDateOfBirth(LocalDate.parse(traineeUpdateData.getDateOfBirth(), FORMATTER));
                    trainee.getUser().setIsActive(traineeUpdateData.getIsActive());
                    trainee.getUser().setUsername(traineeUpdateData.getUsername());
                    trainee.getUser().setFirstName(traineeUpdateData.getFirstname());
                    trainee.getUser().setLastName(traineeUpdateData.getLastname());
                    return TraineeMapper.INSTANCE.toDto(traineeRepository.save(trainee));
                })
                .orElseThrow(() -> new NotFoundException("Trainee not found"));
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
        final var authUsername = securityService.getCurrentUsername();
        return findByUsername(authUsername);
    }

    @Override
    @Transactional
    public TraineeDto changePassword(String oldPassword, String newPassword) throws NotFoundException, CredentialException {
        final var authUsername = securityService.getCurrentUsername();
        return traineeRepository.findByUser_Username(authUsername)
                .map(trainee -> {
                    if (!passwordEncoder.matches(oldPassword, trainee.getUser().getPassword()))
                        throw new CredentialException("Old password does not match");
                    trainee.getUser().setPassword(newPassword);
                    return TraineeMapper.INSTANCE.toDto(traineeRepository.save(trainee));
                })
                .orElseThrow(() -> new NotFoundException("Trainee not found with authUsername " + authUsername));
    }

    @Override
    public TraineeDto findByUsername(String username) throws NotFoundException {
        return TraineeMapper.INSTANCE.toDto(traineeRepository.findByUser_Username(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found by username")));
    }

    @Override
    @Transactional
    public String deleteByUsername(String username) throws NotFoundException {
        return traineeRepository.findByUser_Username(username)
                .map(trainee -> {
                    traineeRepository.delete(trainee);
                    return username;
                })
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINEE.getVal()));
    }

    @Override
    @Transactional
    public TraineeDto changeStatus() throws NotFoundException {
        final var authUsername = securityService.getCurrentUsername();
        return traineeRepository.findByUser_Username(authUsername)
                .map(trainee -> {
                    trainee.getUser().setIsActive(trainee.getUser().getIsActive()
                            .equals(Boolean.TRUE) ? Boolean.FALSE : Boolean.TRUE);
                    return TraineeMapper.INSTANCE.toDto(traineeRepository.save(trainee));
                })
                .orElseThrow(() -> new NotFoundException("Trainee not found"));
    }
}
