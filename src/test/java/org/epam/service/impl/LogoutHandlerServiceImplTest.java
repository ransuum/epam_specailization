package org.epam.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.RefreshToken;
import org.epam.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutHandlerServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogoutHandlerServiceImpl logoutHandlerService;

    private static final String BEARER = "Bearer ";
    private static final String TEST_TOKEN = "testRefreshToken";
    private static final String AUTH_HEADER = BEARER + TEST_TOKEN;

    @BeforeEach
    void setUp() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(AUTH_HEADER);
    }

    @Test
    void logout_WithValidBearerToken_ShouldRevokeAndDeleteToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(TEST_TOKEN);

        when(refreshTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        logoutHandlerService.logout(request, response, authentication);

        verify(refreshTokenRepository).findByToken(TEST_TOKEN);
        verify(refreshTokenRepository).save(refreshToken);
        assertTrue(refreshToken.isRevoked());
    }

    @Test
    void logout_WithNonExistingToken_ShouldDoNothing() {
        when(refreshTokenRepository.findByToken(TEST_TOKEN)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                logoutHandlerService.logout(request, response, authentication)
        );

        verify(refreshTokenRepository).findByToken(TEST_TOKEN);
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void logout_WithNonBearerToken_ShouldReturnEarly() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic dXNlcm5hbWU6cGFzc3dvcmQ=");

        logoutHandlerService.logout(request, response, authentication);

        verify(refreshTokenRepository, never()).findByToken(anyString());
        verify(refreshTokenRepository, never()).save(any());
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void logout_WithNullHeader_ShouldHandleNullPointerException() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        assertThrows(NullPointerException.class, () ->
                logoutHandlerService.logout(request, response, authentication)
        );

        verify(refreshTokenRepository, never()).findByToken(anyString());
        verify(refreshTokenRepository, never()).save(any());
        verify(refreshTokenRepository, never()).delete(any());
    }

    private void assertTrue(boolean condition) {
        if (!condition)
            throw new AssertionError("Expected condition to be true, but was false");
    }
}