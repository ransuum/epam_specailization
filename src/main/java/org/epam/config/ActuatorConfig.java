package org.epam.config;

import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.MapInfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ActuatorConfig {
    @Bean
    public InfoContributor environmentInfoContributor() {
        Map<String, Object> details = new HashMap<>();
        details.put("application", "Epam Specialization");
        details.put("version", "1.0.0");
        return new MapInfoContributor(details);
    }

    @Bean
    @Profile("local")
    public InfoContributor localInfoContributor() {
        Map<String, Object> details = new HashMap<>();
        details.put("environment", "Local Development");
        details.put("contacts", "valerii_dmytrenko1@epam.com");
        return new MapInfoContributor(details);
    }

    @Bean
    @Profile("dev")
    public InfoContributor devInfoContributor() {
        Map<String, Object> details = new HashMap<>();
        details.put("environment", "Development");
        details.put("contacts", "valerii_dmytrenko2@epam.com");
        return new MapInfoContributor(details);
    }

    @Bean
    @Profile("stg")
    public InfoContributor stgInfoContributor() {
        Map<String, Object> details = new HashMap<>();
        details.put("environment", "Staging");
        details.put("contacts", "valerii_dmytrenko3@epam.com");
        return new MapInfoContributor(details);
    }

    @Bean
    @Profile("prod")
    public InfoContributor prodInfoContributor() {
        Map<String, Object> details = new HashMap<>();
        details.put("environment", "Production");
        details.put("contacts", "valerii_dmytrenko4@epam.com");
        return new MapInfoContributor(details);
    }
}
