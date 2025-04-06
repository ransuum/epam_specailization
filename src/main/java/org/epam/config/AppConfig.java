package org.epam.config;

import lombok.RequiredArgsConstructor;
import org.epam.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner addPasswordEncoder() {
        return args -> {
            userRepository.findAll().forEach(u -> {
                u.setPassword(new BCryptPasswordEncoder().encode(u.getPassword()));
                userRepository.save(u);
            });
        };
    }
}
