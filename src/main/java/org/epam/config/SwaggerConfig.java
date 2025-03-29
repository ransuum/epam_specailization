package org.epam.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.configuration.SpringDocDataRestConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"org.springdoc"})
@EnableAutoConfiguration(exclude = SpringDocDataRestConfiguration.class)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI();
    }
}
