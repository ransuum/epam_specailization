package org.epam.service.impl;

import lombok.RequiredArgsConstructor;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.UserDto;
import org.epam.models.dto.create.UserCreateDto;
import org.epam.models.entity.User;
import org.epam.models.enums.NotFoundMessages;
import org.epam.repository.UserRepository;
import org.epam.service.UserService;
import org.epam.utils.CredentialsGenerator;
import org.epam.utils.mappers.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.epam.utils.FieldValidator.check;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CredentialsGenerator credentialsGenerator;

    @Override
    @Transactional
    public UserDto save(UserCreateDto request) {
        var username = credentialsGenerator.generateUsername(request.getFirstName(), request.getLastName());
        return UserMapper.INSTANCE.toDto(userRepository.save(
                User.builder()
                        .lastName(request.getLastName())
                        .firstName(request.getFirstName())
                        .password(credentialsGenerator.generatePassword(username))
                        .username(username)
                        .isActive(request.getIsActive())
                        .build())
        );
    }

    @Override
    @Transactional
    public UserDto update(String id, User request) throws NotFoundException {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.USERS.getVal()));

        if (check(request.getFirstName())) user.setFirstName(request.getFirstName());
        if (check(request.getLastName())) user.setLastName(request.getLastName());
        if (check(request.getIsActive())) user.setIsActive(request.getIsActive());
        String username = credentialsGenerator.generateUsername(request.getFirstName(), request.getLastName());
        String password = credentialsGenerator.generatePassword(username);
        user.setPassword(password);
        user.setUsername(username);
        return UserMapper.INSTANCE.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(String id) {
        var user = userRepository.findById(id).orElseThrow(()
                -> new NotFoundException(NotFoundMessages.USERS.getVal()));
        userRepository.delete(user);
    }

    @Override
    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper.INSTANCE::toDto);
    }

    @Override
    @Transactional
    public UserDto findById(String id) throws NotFoundException {
        return UserMapper.INSTANCE.toDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.USERS.getVal())));

    }

    @Override
    @Transactional
    public User findByUsername(String username) throws NotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("There is no user with this username or password is not correct"));
    }
}
