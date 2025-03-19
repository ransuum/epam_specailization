package org.epam.controller;

import org.epam.models.dto.UserDto;
import org.epam.models.entity.User;
import org.epam.models.request.userRequest.UserRequestCreate;
import org.epam.service.UserService;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public UserDto createUser(Scanner scanner) {
        System.out.print("Enter firstName: ");
        var firstName = scanner.next();
        System.out.print("Enter lastName: ");
        var lastName = scanner.next();
        return userService.save(new UserRequestCreate(firstName, lastName, Boolean.TRUE));
    }

    public UserDto getUser(String id) {
        return userService.findById(id);
    }

    public UserDto updateUser(String id, Scanner scanner) {
        System.out.print("Enter firstName: ");
        var firstName = scanner.nextLine().trim();
        System.out.print("Enter lastName: ");
        var lastName = scanner.nextLine().trim();
        System.out.print("Active?(true/false): ");
        var active = Boolean.valueOf(scanner.nextLine());
        return userService.update(id, new User(firstName, lastName, active));
    }

    public void deleteUser(Scanner scanner) {
        System.out.print("Enter ID: ");
        var id = scanner.next();
        userService.delete(id);
    }

    public void findAll() {
        userService.findAll().forEach(System.out::println);
    }
}
