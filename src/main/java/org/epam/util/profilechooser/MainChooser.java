package org.epam.util.profilechooser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.enums.Profile;
import org.epam.util.render.MenuRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MainChooser implements Chooser {
    private final MenuRenderer menuRenderer;

    @Value("${spring.profiles.active}")
    private String profile;

    private static final Log log = LogFactory.getLog(MainChooser.class);

    public MainChooser(MenuRenderer menuRenderer) {
        this.menuRenderer = menuRenderer;
    }

    @Override
    public void initialize() throws InterruptedException {
        log.info("Initializing application");
        process();
    }

    @Override
    public void process() throws InterruptedException {
        menuRenderer.renderAndProcessMenu();
    }

    @Override
    public boolean getProfile() {
        return profile.equals(Profile.main.name());
    }
}
