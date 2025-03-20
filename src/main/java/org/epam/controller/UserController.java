package org.epam.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.models.dto.UserDto;
import org.epam.models.entity.User;
import org.epam.models.request.userrequest.UserRequestCreate;
import org.epam.service.UserService;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
public class UserController {
    private final UserService userService;
    private static final Logger logger = LogManager.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public UserDto createUser(Scanner scanner) {
        try {
            System.out.print("Enter firstName: ");
            var firstName = scanner.next();
            System.out.print("Enter lastName: ");
            var lastName = scanner.next();
            return userService.save(new UserRequestCreate(firstName, lastName, Boolean.TRUE));
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            return null;
        }
    }

    public UserDto getUser(String id) {
        try {
            return userService.findById(id);
        } catch (Exception e) {
            logger.error("Error fetching user with ID: {}", e.getMessage());
            return null;
        }
    }

    public UserDto updateUser(String id, Scanner scanner) {
        try {
            System.out.print("Enter firstName: ");
            var firstName = scanner.nextLine().trim();
            System.out.print("Enter lastName: ");
            var lastName = scanner.nextLine().trim();
            System.out.print("Active?(true/false): ");
            var active = Boolean.valueOf(scanner.nextLine());
            return userService.update(id, new User(firstName, lastName, active));
        } catch (Exception e) {
            logger.error("Error updating user with ID: {}", e.getMessage());
            return null;
        }
    }

    public void deleteUser(Scanner scanner) {
        try {
            System.out.print("Enter ID: ");
            var id = scanner.next();
            userService.delete(id);
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage());
        }
    }

    public void findAll() {
        try {
            userService.findAll().forEach(System.out::println);
        } catch (Exception e) {
            logger.error("Error fetching all users: {}", e.getMessage());
        }
    }
}
