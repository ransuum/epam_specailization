package org.epam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.MapInfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ActuatorConfig {
    @Value("${app.info.environment}")
    private String environment;

    @Value("${app.info.contacts}")
    private String contacts;

    @Bean
    public InfoContributor environmentInfoContributor() {
        Map<String, Object> details = new HashMap<>();
        details.put("application", "Epam Specialization");
        details.put("version", "1.0.0");
        return new MapInfoContributor(details);
    }

    @Bean
    public InfoContributor localInfoContributor() {
        Map<String, Object> details = new HashMap<>();
        details.put("environment", environment);
        details.put("contacts", contacts);
        return new MapInfoContributor(details);
    }
}
