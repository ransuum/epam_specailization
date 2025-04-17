package org.epam.service;

import jakarta.servlet.http.HttpServletResponse;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.create.TraineeCreateDto;
import org.epam.models.dto.create.TrainerCreateDto;
import org.epam.models.entity.RefreshToken;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.User;
import org.epam.repository.RefreshTokenRepository;
import org.epam.security.jwt.JwtTokenGenerator;
import org.epam.service.impl.AuthenticationServiceImpl;
import org.epam.utils.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenGenerator jwtTokenGenerator;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User testUser;
    private Trainee testTrainee;
    private Trainer testTrainer;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .isActive(true)
                .password("password")
                .roles("ROLE_USER")
                .build();

        testTrainee = Trainee.builder()
                .user(testUser)
                .build();

        testTrainer = Trainer.builder()
                .user(testUser)
                .build();

        testRefreshToken = RefreshToken.builder()
                .token("refreshToken")
                .user(testUser)
                .expiresAt(Instant.now().plusSeconds(86400))
                .revoked(false)
                .build();
    }

    @Test
    void getJwtTokensAfterAuthentication_Success() {
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(jwtTokenGenerator.generateAccessToken(authentication)).thenReturn("accessToken");
        when(jwtTokenGenerator.generateRefreshToken(authentication)).thenReturn("refreshToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);
        doNothing().when(jwtTokenGenerator).creatRefreshTokenCookie(response, "refreshToken");

        var result = authenticationService.getJwtTokensAfterAuthentication(authentication, response);

        assertNotNull(result);
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
        assertEquals("testuser", result.username());
        assertEquals(15 * 60, result.accessTokenExpiry());
        assertEquals(TokenType.BEARER, result.tokenType());

        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(jwtTokenGenerator).creatRefreshTokenCookie(response, "refreshToken");
    }

    @Test
    void getJwtTokensAfterAuthentication_ThrowsExceptionOnError() {
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenThrow(new RuntimeException("Database error"));

        var exception = assertThrows(ResponseStatusException.class, () ->
                authenticationService.getJwtTokensAfterAuthentication(authentication, response)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Please Try Again", exception.getReason());
    }

    @Test
    void getAccessTokenUsingRefreshToken_Success() {
        String authHeader = "Bearer refreshToken";
        when(refreshTokenRepository.findByToken("refreshToken")).thenReturn(Optional.of(testRefreshToken));
        when(jwtTokenGenerator.createAuthenticationObject(testUser)).thenReturn(authentication);
        when(jwtTokenGenerator.generateAccessToken(authentication)).thenReturn("newAccessToken");
        when(jwtTokenGenerator.createRefreshToken(eq(testUser), any(Authentication.class))).thenReturn(testRefreshToken);
        when(refreshTokenRepository.save(testRefreshToken)).thenReturn(testRefreshToken);

        var result = (AuthResponseDto) authenticationService.getAccessTokenUsingRefreshToken(authHeader);

        assertNotNull(result);
        assertEquals("newAccessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
        assertEquals("testuser", result.username());
        assertEquals(5 * 60, result.accessTokenExpiry());
        assertEquals(TokenType.BEARER, result.tokenType());
    }

    @Test
    void getAccessTokenUsingRefreshToken_InvalidTokenFormat() {
        String invalidHeader = "InvalidToken";

        var exception = assertThrows(ResponseStatusException.class, () ->
                authenticationService.getAccessTokenUsingRefreshToken(invalidHeader)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid token format", exception.getReason());
    }

    @Test
    void getAccessTokenUsingRefreshToken_TokenRevoked() {
        final String authHeader = "Bearer refreshToken";
        testRefreshToken.setRevoked(true);
        when(refreshTokenRepository.findByToken("refreshToken")).thenReturn(Optional.of(testRefreshToken));

        var exception = assertThrows(ResponseStatusException.class, () ->
                authenticationService.getAccessTokenUsingRefreshToken(authHeader)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Refresh token revoked", exception.getReason());
    }

    @Test
    void registerTrainee_Success() {
        var traineeCreateDto = new TraineeCreateDto("Test", "User", "01-01-1990", "Test Address");
        when(traineeService.save(traineeCreateDto)).thenReturn(testTrainee);
        when(jwtTokenGenerator.createAuthenticationObject(testUser)).thenReturn(authentication);
        when(jwtTokenGenerator.generateAccessToken(authentication)).thenReturn("accessToken");
        when(jwtTokenGenerator.generateRefreshToken(authentication)).thenReturn("refreshToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        var result = authenticationService.registerTrainee(traineeCreateDto, response);

        assertNotNull(result);
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
        assertEquals("testuser", result.username());
        assertEquals(5 * 60, result.accessTokenExpiry());
        assertEquals(TokenType.BEARER, result.tokenType());

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void registerTrainer_Success() {
        var trainerCreateDto = new TrainerCreateDto("Test", "User", "Specialization");
        when(trainerService.save(trainerCreateDto)).thenReturn(testTrainer);
        when(jwtTokenGenerator.createAuthenticationObject(testUser)).thenReturn(authentication);
        when(jwtTokenGenerator.generateAccessToken(authentication)).thenReturn("accessToken");
        when(jwtTokenGenerator.generateRefreshToken(authentication)).thenReturn("refreshToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        var result = authenticationService.registerTrainer(trainerCreateDto, response);

        assertNotNull(result);
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
        assertEquals("testuser", result.username());
        assertEquals(5 * 60, result.accessTokenExpiry());
        assertEquals(TokenType.BEARER, result.tokenType());

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
}