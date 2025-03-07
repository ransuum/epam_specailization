package org.epam;

import org.epam.config.AppConfig;
import org.epam.models.enums.Profile;
import org.epam.util.profile_chooser.Chooser;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SpringCoreTask {
    private final Map<Profile, Chooser> choosers;

    public SpringCoreTask(List<Chooser> choosers) {
        this.choosers = choosers.stream()
                .collect(Collectors.toMap(Chooser::getProfile, e -> e));
    }

    public void start(AnnotationConfigApplicationContext context) {
        try {
            choosers.get(Profile.MAIN).initialize(context);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        SpringCoreTask springCoreTask = context.getBean(SpringCoreTask.class);
        springCoreTask.start(context);
    }
}