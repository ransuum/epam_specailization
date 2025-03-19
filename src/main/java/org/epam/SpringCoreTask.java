package org.epam;

import org.epam.config.AppConfig;
import org.epam.utils.menurender.Menu;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringCoreTask {

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            var menu = context.getBean(Menu.class);
            menu.show();
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
        }
    }
}