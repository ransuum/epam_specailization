package org.epam.config;

import jakarta.persistence.EntityManager;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class PostProcessing implements BeanPostProcessor {
    private static final Logger log = LogManager.getLogger(PostProcessing.class);

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean,@NonNull String beanName) throws BeansException {
        if (bean instanceof SessionFactory) log.info("Initializing SessionFactory: {}", beanName);
        if (bean instanceof EntityManager) log.info("Initializing EntityManager: {}", beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof SessionFactory) log.info("SessionFactory initialized: {}", beanName);
        if (bean instanceof EntityManager) log.info("EntityManager initialized: {}", beanName);
        return bean;
    }
}
