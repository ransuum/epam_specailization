package org.epam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Автентифікація користувача",
            description = """
                         Використовуйте Basic Auth у Postman:
                         1. Перейдіть до вкладки Authorization та оберіть `Basic Auth`
                         2. Введіть email та пароль
                         3. Вкажіть URL: `http://localhost:8000/sign-in` або `https://favourable-rodie-java-service-b82e5859.koyeb.app/sign-in`
                         4. Оберіть метод `POST` та натисніть `Send`
                        \s
                         Якщо використовуєте swagger, зверху є кнопка Authorize.
                         У відповіді прийде access-токен. Використовуйте його для наступних запитів, передаючи в заголовку `Authorization: Bearer {token}`.
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Успішна автентифікація. Повертає access та refresh токени."),
                    @ApiResponse(responseCode = "401", description = "Невірні облікові дані")
            }
    )

    @PostMapping("/sign-in")
    public ResponseEntity<Object> authenticateUser(Authentication authentication, HttpServletResponse response) {
        return new ResponseEntity<>(authenticationService.getJwtTokensAfterAuthentication(authentication, response), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Оновлення Access-токену",
            description = """
                        Використовуйте Refresh-токен для отримання нового Access-токену.
                        У запиті передайте `Authorization: Bearer {refresh_token}`.
                    """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Новий Access-токен успішно отримано"),
                    @ApiResponse(responseCode = "403", description = "Refresh-токен недійсний або закінчився термін дії")
            }
    )
    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping("/refresh-token")
    public ResponseEntity<Object> getAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(authenticationService.getAccessTokenUsingRefreshToken(authorizationHeader));
    }

    @Operation(
            summary = "Реєстрація нового користувача",
            description = """
                        Дозволяє зареєструвати нового користувача.
                        У тілі запиту передавайте JSON з необхідними даними.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Дані користувача для реєстрації",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TraineeCreateDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Користувача успішно зареєстровано"),
                    @ApiResponse(responseCode = "400", description = "Помилка валідації вхідних даних")
            }
    )
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
