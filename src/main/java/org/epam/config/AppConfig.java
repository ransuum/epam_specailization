package org.epam.config;

import org.epam.models.SecurityContextHolder;
import org.epam.models.enums.UserType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AppConfig {
    @Bean
    public SecurityContextHolder securityContextHolder() {
        return SecurityContextHolder.builder()
                .userType(UserType.NOT_AUTHORIZE)
                .build();
    }
}
