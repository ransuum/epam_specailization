package org.epam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.epam.models.dto.create.TraineeCreateDto;
import org.epam.models.dto.create.TrainerCreateDto;
import org.epam.service.AuthenticationService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "User Authentication",
            description = """
                     Use Basic Auth in Postman:
                     1. Go to the Authorization tab and select `Basic Auth`
                     2. Enter username and password
                     3. Specify URL: `http://localhost:8000/sign-in`
                     4. Select `POST` method and click `Send`
                     5. Response: access token, refresh token and details
                     6. Add access token to Bearer
                    \s
                     Use Basic Auth in Swagger:
                     1. Go to the icon lock and use `Basic Auth`
                     2. Enter username and password
                     3. Select `POST` method and click `Send`
                     4. Response: access token, refresh token and details
                     5. Add access token to Bearer auth in swagger
                \s""",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successful authentication. Returns access and refresh tokens."),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    @PostMapping("/sign-in")
    public ResponseEntity<Object> authenticateUser(Authentication authentication, HttpServletResponse response) {
        return new ResponseEntity<>(authenticationService.getJwtTokensAfterAuthentication(authentication, response), HttpStatus.CREATED);
    }

    @Operation(
            summary = "refresh-token",
            description = """
                     Use refresh-token in Postman:
                     1. Go to the Authorization tab and select `Bearer`
                     2. Specify URL: `http://localhost:8000/refresh-token` POST
                     3. Click execute
                     4. Response: access token, refresh token and details
                    \s
                     Use refresh-token in Swagger:
                     1. Go to the icon lock and select `Bearer` -> put refresh token in there:
                     2. Write in param refresh token too
                     3. Click execute
                     4. Response: access token, refresh token and details
                \s"""
    )
    @PreAuthorize("hasAuthority('REFRESH_TOKEN')")
    @PostMapping("/refresh-token")
    public ResponseEntity<Object> getAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(authenticationService.getAccessTokenUsingRefreshToken(authorizationHeader));
    }

    @PostMapping("/sign-up/trainee")
    public ResponseEntity<Object> registerTrainee(@Valid @RequestBody TraineeCreateDto traineeCreateDto,
                                          BindingResult bindingResult, HttpServletResponse httpServletResponse) {
        log.info("[AuthController:registerUser]Signup Process Started for Trainee:{}",
                traineeCreateDto.firstname() + " " + traineeCreateDto.lastname());
        if (bindingResult.hasErrors()) {
            final List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[AuthController:registerTrainee]Errors in user:{}", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        return new ResponseEntity<>(authenticationService.registerTrainee(traineeCreateDto, httpServletResponse), HttpStatus.CREATED);
    }

    @PostMapping("/sign-up/trainer")
    public ResponseEntity<Object> registerTrainer(@Valid @RequestBody TrainerCreateDto trainerCreateDto,
                                             BindingResult bindingResult, HttpServletResponse httpServletResponse) {
        log.info("[AuthController:registerUser]Signup Process Started for Trainer:{}",
                trainerCreateDto.firstname() + " " + trainerCreateDto.lastname());
        if (bindingResult.hasErrors()) {
            final List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[AuthController:registerTrainer]Errors in user:{}", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        return new ResponseEntity<>(authenticationService.registerTrainer(trainerCreateDto, httpServletResponse), HttpStatus.CREATED);
    }
}
