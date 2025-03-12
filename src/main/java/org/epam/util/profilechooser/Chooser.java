package org.epam.util.profilechooser;

public interface Chooser {
    void initialize() throws InterruptedException;
    void process() throws InterruptedException;
    boolean getProfile();
}
