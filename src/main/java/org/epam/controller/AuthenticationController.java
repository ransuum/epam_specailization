package org.epam.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.epam.models.SecurityContextHolder;
import org.epam.models.enums.UserType;
import org.epam.models.request.AuthRequest;
import org.epam.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final SecurityContextHolder securityContextHolder;

    @PostMapping("/sign-in")
    public ResponseEntity<String> authenticate(@RequestBody @Valid AuthRequest authRequest) {
        var temp = authenticationService.authenticate(authRequest.username(), authRequest.password());
        this.securityContextHolder.setUsername(temp.getUsername());
        this.securityContextHolder.setUserId(temp.getUserId());
        this.securityContextHolder.setGenerateAt(temp.getGenerateAt());
        this.securityContextHolder.setExpiredAt(temp.getExpiredAt());
        this.securityContextHolder.setUserType(temp.getUserType());
        log.info("Authentication successful");
        return ResponseEntity.ok("Authentication successful");
    }

    @PostMapping("/sign-out")
    public ResponseEntity<String> logout() {
        this.securityContextHolder.setUsername(null);
        this.securityContextHolder.setUserId(null);
        this.securityContextHolder.setGenerateAt(null);
        this.securityContextHolder.setExpiredAt(null);
        this.securityContextHolder.setUserType(UserType.NOT_AUTHORIZE);
        log.info("Logout successful");
        return ResponseEntity.ok("Logout successful");
    }
}
