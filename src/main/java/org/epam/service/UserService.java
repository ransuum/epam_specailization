package org.epam.service;


import org.epam.models.dto.UserDto;
import org.epam.models.entity.User;
import org.epam.models.request.userRequest.UserRequestCreate;

import java.util.List;

public interface UserService {
    UserDto save(UserRequestCreate request);

    UserDto update(String id, User request);

    void delete(String id);

    List<UserDto> findAll();

    UserDto findById(String id);
}
