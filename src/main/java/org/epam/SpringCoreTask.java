package org.epam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringCoreTask {
    public static void main(String[] args) {
        SpringApplication.run(SpringCoreTask.class, args);
    }
}