package org.epam.security.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @InjectMocks
    private SecurityService securityService;

    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        securityContext = new SecurityContextImpl();
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsername_WithAuthenticatedUser_ReturnsUsername() {
        authentication = new UsernamePasswordAuthenticationToken("testUser", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        securityContext.setAuthentication(authentication);

        final String username = securityService.getCurrentUsername();

        assertEquals("testUser", username);
    }

    @Test
    void getCurrentUsername_WithoutAuthentication_ThrowsException() {
        securityContext.setAuthentication(null);

        assertThrows(AuthenticationCredentialsNotFoundException.class,
                () -> securityService.getCurrentUsername());
    }

    @Test
    void getCurrentUsername_WithUnauthenticatedUser_ThrowsException() {
        authentication = new UsernamePasswordAuthenticationToken("testUser", "password");
        authentication.setAuthenticated(false);
        securityContext.setAuthentication(authentication);

        assertThrows(AuthenticationCredentialsNotFoundException.class,
                () -> securityService.getCurrentUsername());
    }

    @Test
    void getCurrentUserRoles_WithAuthenticatedUser_ReturnsRoles() {
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        authentication = new UsernamePasswordAuthenticationToken("testUser", "password", authorities);
        securityContext.setAuthentication(authentication);

        Collection<SimpleGrantedAuthority> roles = securityService.getCurrentUserRoles();

        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(roles.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void getCurrentUserRoles_WithoutAuthentication_ThrowsException() {
        securityContext.setAuthentication(null);

        assertThrows(AuthenticationCredentialsNotFoundException.class,
                () -> securityService.getCurrentUserRoles());
    }

    @Test
    void hasRole_WhenUserHasRole_ReturnsTrue() {
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        authentication = new UsernamePasswordAuthenticationToken("testUser", "password", authorities);
        securityContext.setAuthentication(authentication);

        assertTrue(securityService.hasRole("ROLE_ADMIN"));
    }

    @Test
    void hasRole_WhenUserDoesNotHaveRole_ReturnsFalse() {
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        authentication = new UsernamePasswordAuthenticationToken("testUser", "password", authorities);
        securityContext.setAuthentication(authentication);

        assertFalse(securityService.hasRole("ROLE_ADMIN"));
    }
}