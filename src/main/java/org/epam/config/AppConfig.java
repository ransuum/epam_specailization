package org.epam.config;

import org.epam.models.SecurityContextHolder;
import org.epam.models.enums.UserType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public SecurityContextHolder securityContextHolder() {
        return SecurityContextHolder.builder()
                .userType(UserType.NOT_AUTHORIZE)
                .build();
    }
}
