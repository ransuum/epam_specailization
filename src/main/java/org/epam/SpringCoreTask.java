package org.epam;

import org.epam.security.rsa.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RSAKeyRecord.class)
public class SpringCoreTask {

    public static void main(String[] args) {
        SpringApplication.run(SpringCoreTask.class, args);
    }
}