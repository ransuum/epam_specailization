package org.epam;

import org.epam.config.AppConfig;
import org.epam.util.profilechooser.Chooser;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;
import java.util.stream.Collectors;

public class SpringCoreTask {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            Map<Boolean, Chooser> choosers = context.getBeansOfType(Chooser.class).values().stream()
                    .collect(Collectors.toMap(Chooser::getProfile, chooser -> chooser));

            try {
                choosers.get(true).initialize();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}