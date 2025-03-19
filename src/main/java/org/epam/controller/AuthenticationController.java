package org.epam.controller;

import org.epam.models.SecurityContextHolder;
import org.epam.models.enums.UserType;
import org.epam.service.AuthenticationService;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final SecurityContextHolder securityContextHolder;

    public AuthenticationController(AuthenticationService authenticationService,
                                    SecurityContextHolder securityContextHolder) {
        this.authenticationService = authenticationService;
        this.securityContextHolder = securityContextHolder;
    }

    public SecurityContextHolder authenticate(Scanner scanner) {
        System.out.print("Please enter your username: ");
        String username = scanner.next();
        System.out.print("Please enter your password: ");
        String password = scanner.next();

        SecurityContextHolder temp = authenticationService.authenticate(username, password);

        this.securityContextHolder.setUsername(temp.getUsername());
        this.securityContextHolder.setUserId(temp.getUserId());
        this.securityContextHolder.setGenerateAt(temp.getGenerateAt());
        this.securityContextHolder.setExpiredAt(temp.getExpiredAt());
        this.securityContextHolder.setUserType(temp.getUserType());
        return this.securityContextHolder;
    }

    public SecurityContextHolder logout() {
        this.securityContextHolder.setUsername(null);
        this.securityContextHolder.setUserId(null);
        this.securityContextHolder.setGenerateAt(null);
        this.securityContextHolder.setExpiredAt(null);
        this.securityContextHolder.setUserType(UserType.NOT_AUTHORIZE);
        return this.securityContextHolder;
    }
}
