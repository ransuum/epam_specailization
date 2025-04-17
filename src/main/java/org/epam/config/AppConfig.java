package org.epam.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.epam.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AppConfig {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        final var converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("ep");
        converter.setAuthorityPrefix("");

        final var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public CommandLineRunner addPasswordEncoder() {
        return args ->
                userRepository.findAll().forEach(u -> {
                    String currentPassword = u.getPassword();
                    if (!currentPassword.startsWith("$2")) {
                        u.setPassword(passwordEncoder.encode(currentPassword));
                        userRepository.save(u);
                        log.info("Encoded password for user: {}", u.getUsername());
                    } else log.debug("Password already encoded for user: {}", u.getUsername());
                });
    }
}
