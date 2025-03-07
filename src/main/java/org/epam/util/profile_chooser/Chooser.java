package org.epam.util.profile_chooser;

import org.epam.models.enums.Profile;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public interface Chooser {
    void initialize(AnnotationConfigApplicationContext context) throws InterruptedException;
    void process() throws InterruptedException;
    Profile getProfile();
}
