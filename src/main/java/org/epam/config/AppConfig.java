package org.epam.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.*;

@Configuration
@ComponentScan(value = "org.epam")
public class AppConfig {

    @Bean
    public Map<String, Map<Integer, Object>> storageMap() {
        Map<String, Map<Integer, Object>> storage = new HashMap<>();
        storage.put("trainees", new HashMap<>());
        storage.put("trainers", new HashMap<>());
        storage.put("trainings", new HashMap<>());
        return storage;
    }

    @Bean
    public ObjectMapper beanMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(false);
        return configurer;
    }
}
