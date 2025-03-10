package org.epam.util.profilechooser;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public interface Chooser {
    void initialize(AnnotationConfigApplicationContext context) throws InterruptedException;
    void process() throws InterruptedException;
    boolean getProfile();
}
