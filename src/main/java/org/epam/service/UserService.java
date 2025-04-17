package org.epam.service;

import org.epam.models.dto.UserDto;
import org.epam.models.entity.User;
import org.epam.models.dto.create.UserCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto save(UserCreateDto request);

    UserDto update(String id, User request);

    void delete(String id);

    Page<UserDto> findAll(Pageable pageable);

    UserDto findById(String id);

    User findByUsername(String username);
}
