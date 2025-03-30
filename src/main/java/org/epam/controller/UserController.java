package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.epam.models.dto.UserDto;
import org.epam.models.enums.UserType;
import org.epam.service.UserService;
import org.epam.utils.permissionforroles.RequiredRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
@Tag(name = "User Management", description = "APIs for managing user operations")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<UserDto> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/all")
    @RequiredRole({UserType.TRAINEE, UserType.TRAINER})
    public ResponseEntity<List<UserDto>> findAll() {
       return ResponseEntity.ok(userService.findAll());
    }
}
