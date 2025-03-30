package org.epam.controller;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.models.SecurityContextHolder;
import org.epam.models.enums.UserType;
import org.epam.models.request.AuthRequest;
import org.epam.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final SecurityContextHolder securityContextHolder;
    private static final Logger logger = LogManager.getLogger(AuthenticationController.class);

    public AuthenticationController(AuthenticationService authenticationService,
                                    SecurityContextHolder securityContextHolder) {
        this.authenticationService = authenticationService;
        this.securityContextHolder = securityContextHolder;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthRequest authRequest) {
        var temp = authenticationService.authenticate(authRequest.username(), authRequest.password());
        this.securityContextHolder.setUsername(temp.getUsername());
        this.securityContextHolder.setUserId(temp.getUserId());
        this.securityContextHolder.setGenerateAt(temp.getGenerateAt());
        this.securityContextHolder.setExpiredAt(temp.getExpiredAt());
        this.securityContextHolder.setUserType(temp.getUserType());
        logger.info("Authentication successful");
        return ResponseEntity.ok("Authentication successful");
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> logout() {
        this.securityContextHolder.setUsername(null);
        this.securityContextHolder.setUserId(null);
        this.securityContextHolder.setGenerateAt(null);
        this.securityContextHolder.setExpiredAt(null);
        this.securityContextHolder.setUserType(UserType.NOT_AUTHORIZE);
        logger.info("Logout successful");
        return ResponseEntity.ok("Logout successful");
    }
}
