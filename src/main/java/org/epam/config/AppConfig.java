package org.epam.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import org.epam.models.SecurityContextHolder;
import org.epam.models.entity.*;
import org.epam.models.enums.UserType;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;


@Configuration
@ComponentScan(basePackages = {"org.epam", "org.epam.models.entity"})
public class AppConfig {
    @Bean
    public SecurityContextHolder securityContextHolder() {
        return SecurityContextHolder.builder()
                .userType(UserType.NOT_AUTHORIZE)
                .build();
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setConfigLocation(new ClassPathResource("hibernate.cfg.xml"));
        sessionFactory.setAnnotatedClasses(User.class, Trainee.class, Training.class, TrainingType.class, Trainer.class);
        return sessionFactory;
    }

    @Bean
    public EntityManager entityManager(SessionFactory sessionFactory) {
        return sessionFactory.createEntityManager();
    }
}
