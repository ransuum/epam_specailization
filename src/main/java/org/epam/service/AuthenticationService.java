package org.epam.service;


import jakarta.servlet.http.HttpServletResponse;
import org.epam.models.RegistrationResponseDto;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.create.TraineeCreateDto;
import org.epam.models.dto.create.TrainerCreateDto;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {
    AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response);

    Object getAccessTokenUsingRefreshToken(String authorizationHeader);

    RegistrationResponseDto registerTrainee(TraineeCreateDto traineeCreateDto, HttpServletResponse httpServletResponse);

    RegistrationResponseDto registerTrainer(TrainerCreateDto traineeCreateDto, HttpServletResponse httpServletResponse);
}
