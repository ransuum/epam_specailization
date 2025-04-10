package org.epam.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.epam.models.entity.RefreshToken;
import org.epam.models.entity.User;
import org.epam.models.enums.Roles;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtTokenGenerator {
    private final JwtEncoder jwtEncoder;

    public JwtTokenGenerator(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public Authentication createAuthenticationObject(User users) {
        String username = users.getUsername();
        String password = users.getPassword();
        String roles = users.getRoles();

        String[] roleArray = roles.split(",");
        GrantedAuthority[] authorities = Arrays.stream(roleArray)
                .map(role -> (GrantedAuthority) role::trim)
                .toArray(GrantedAuthority[]::new);

        return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList(authorities));
    }

    public String generateAccessToken(Authentication authentication) {

        log.info("[JwtTokenGenerator:generateAccessToken] Token Creation Started for:{}", authentication.getName());

        String roles = getRolesOfUser(authentication);

        String permissions = getPermissionsFromRoles(roles);

        var claims = JwtClaimsSet.builder()
                .issuer("chat-engly")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim("scope", permissions)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public void creatRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(15 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);
    }


    public String generateRefreshToken(Authentication authentication) {

        log.info("[JwtTokenGenerator:generateRefreshToken] Token Creation Started for:{}", authentication.getName());

        var claims = JwtClaimsSet.builder()
                .issuer("chat-engly")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(25, ChronoUnit.DAYS))
                .subject(authentication.getName())
                .claim("scope", "REFRESH_TOKEN")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private static String getRolesOfUser(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

    private String getPermissionsFromRoles(String roles) {
        var roleList = Arrays.stream(roles.split(","))
                .map(String::trim)
                .toList();

        return String.join(" ", Roles.getPermissionsForRoles(roleList));
    }

    public RefreshToken createRefreshToken(User users, Authentication authentication) {
        return RefreshToken.builder()
                .user(users)
                .token(generateRefreshToken(authentication))
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(25, ChronoUnit.DAYS))
                .revoked(false)
                .build();
    }

}
