package org.epam.service.impl;

import org.epam.exception.NotFoundException;
import org.epam.models.dto.UserDto;
import org.epam.models.entity.User;
import org.epam.models.request.userrequest.UserRequestCreate;
import org.epam.repository.UserRepository;
import org.epam.service.UserService;
import org.epam.utils.CredentialsGenerator;
import org.epam.utils.mappers.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.utils.CheckerField.check;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CredentialsGenerator credentialsGenerator;

    public UserServiceImpl(UserRepository userRepository, CredentialsGenerator credentialsGenerator) {
        this.userRepository = userRepository;
        this.credentialsGenerator = credentialsGenerator;
    }

    @Override
    public UserDto save(UserRequestCreate request) {
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
    public UserDto update(String id, User request) throws NotFoundException {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (check(request.getFirstName())) user.setFirstName(request.getFirstName());
        if (check(request.getLastName())) user.setLastName(request.getLastName());
        if (check(request.getIsActive())) user.setIsActive(request.getIsActive());
        String username = credentialsGenerator.generateUsername(request.getFirstName(), request.getLastName());
        String password = credentialsGenerator.generatePassword(username);
        user.setPassword(password);
        user.setUsername(username);
        return UserMapper.INSTANCE.toDto(userRepository.update(id, user));
    }

    @Override
    public void delete(String id) {
        userRepository.delete(id);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public UserDto findById(String id) throws NotFoundException {
        return UserMapper.INSTANCE.toDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));

    }

    @Override
    public UserDto findByUsername(String username) throws NotFoundException {
        return UserMapper.INSTANCE.toDto(userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("There is no user with this username!")));
    }
}
