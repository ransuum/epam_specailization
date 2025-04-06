package org.epam.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.epam.models.dto.create.TraineeCreateDto;
import org.epam.models.dto.create.TrainerCreateDto;
import org.epam.service.AuthenticationService;
import org.epam.service.LogoutHandlerService;
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
    private final LogoutHandlerService logoutHandlerService;


    @PostMapping("/sign-in")
    public ResponseEntity<Object> authenticateUser(Authentication authentication, HttpServletResponse response) {
        return new ResponseEntity<>(authenticationService.getJwtTokensAfterAuthentication(authentication, response), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping("/refresh-token")
    public ResponseEntity<Object> getAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(authenticationService.getAccessTokenUsingRefreshToken(authorizationHeader));
    }

    @PostMapping("/sign-up/trainee")
    public ResponseEntity<?> registerTrainee(@Valid @RequestBody TraineeCreateDto traineeCreateDto,
                                          BindingResult bindingResult, HttpServletResponse httpServletResponse) {

        log.info("[AuthController:registerUser]Signup Process Started for Trainee:{}",
                traineeCreateDto.firstname() + " " + traineeCreateDto.lastname());
        if (bindingResult.hasErrors()) {
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[AuthController:registerTrainee]Errors in user:{}", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        return new ResponseEntity<>(authenticationService.registerTrainee(traineeCreateDto, httpServletResponse), HttpStatus.CREATED);
    }

    @PostMapping("/sign-up/trainer")
    public ResponseEntity<?> registerTrainer(@Valid @RequestBody TrainerCreateDto trainerCreateDto,
                                             BindingResult bindingResult, HttpServletResponse httpServletResponse) {

        log.info("[AuthController:registerUser]Signup Process Started for Trainer:{}",
                trainerCreateDto.firstname() + " " + trainerCreateDto.lastname());
        if (bindingResult.hasErrors()) {
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[AuthController:registerTrainer]Errors in user:{}", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        return new ResponseEntity<>(authenticationService.registerTrainer(trainerCreateDto, httpServletResponse), HttpStatus.CREATED);
    }
}
