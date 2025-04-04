package org.epam.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.epam.models.dto.AuthDto;
import org.epam.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-in")
    public ResponseEntity<String> authenticate(@RequestBody @Valid AuthDto authDto) {
        authenticationService.authenticate(authDto.username(), authDto.password());
        return ResponseEntity.ok("Authentication successful");
    }

    @PostMapping("/sign-out")
    public ResponseEntity<String> logout() {
        authenticationService.logout();
        return ResponseEntity.ok("Logout successful");
    }
}
