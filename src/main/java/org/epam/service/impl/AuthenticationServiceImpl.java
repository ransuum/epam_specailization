package org.epam.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.create.TraineeCreateDto;
import org.epam.models.dto.create.TrainerCreateDto;
import org.epam.models.entity.RefreshToken;
import org.epam.repository.RefreshTokenRepository;
import org.epam.security.jwt.JwtTokenGenerator;
import org.epam.service.AuthenticationService;
import org.epam.service.TraineeService;
import org.epam.service.TrainerService;
import org.epam.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.epam.utils.TokenType.BEARER;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final UserService userService;

    @Override
    public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response) {
        try {
            final var user = userService.findByUsername(authentication.getName());
            final String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            final String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

            refreshTokenRepository.save(RefreshToken.builder()
                    .user(user)
                    .token(refreshToken)
                    .revoked(false)
                    .createdAt(Instant.now())
                    .expiresAt(Instant.now().plus(25, ChronoUnit.DAYS))
                    .build());

            jwtTokenGenerator.creatRefreshTokenCookie(response, refreshToken);
            log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", user.getUsername());
            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .accessTokenExpiry(15 * 60)
                    .username(user.getUsername())
                    .tokenType(BEARER)
                    .build();

        } catch (Exception e) {
            log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :{}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please Try Again");
        }
    }

    @Override
    public Object getAccessTokenUsingRefreshToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token format");
        final String refreshToken = authorizationHeader.substring(7);

        final var refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .filter(tokens -> !tokens.isRevoked())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token revoked"));
        final var user = refreshTokenEntity.getUser();
        refreshTokenRepository.delete(refreshTokenEntity);

        final var authentication = jwtTokenGenerator.createAuthenticationObject(user);
        final String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(5 * 60)
                .username(user.getUsername())
                .tokenType(BEARER)
                .refreshToken(refreshTokenRepository.save(jwtTokenGenerator
                        .createRefreshToken(user, authentication)).getToken())
                .build();
    }

    @Override
    public AuthResponseDto registerTrainee(TraineeCreateDto traineeCreateDto, HttpServletResponse httpServletResponse) {
        final var userTrainee = traineeService.save(traineeCreateDto).getUser();
        final var authentication = jwtTokenGenerator.createAuthenticationObject(userTrainee);

        final String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
        final String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .user(userTrainee)
                .expiresAt(Instant.now().plus(25, ChronoUnit.DAYS))
                .revoked(false)
                .build());

        log.info("[AuthService:registerUser] Trainee:{} Successfully registered", userTrainee.getUsername());
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(userTrainee.getUsername())
                .accessTokenExpiry(5 * 60)
                .tokenType(BEARER)
                .build();
    }

    @Override
    public AuthResponseDto registerTrainer(TrainerCreateDto trainerCreateDto, HttpServletResponse httpServletResponse) {
        final var userTrainer = trainerService.save(trainerCreateDto).getUser();
        final var authentication = jwtTokenGenerator.createAuthenticationObject(userTrainer);

        final String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
        final String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .user(userTrainer)
                .expiresAt(Instant.now().plus(25, ChronoUnit.DAYS))
                .revoked(false)
                .build());

        log.info("[AuthService:registerUser] Trainer:{} Successfully registered", userTrainer.getUsername());
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(userTrainer.getUsername())
                .accessTokenExpiry(5 * 60)
                .tokenType(BEARER)
                .build();
    }
}
