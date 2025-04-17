package org.epam;

import org.epam.security.rsa.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RSAKeyRecord.class)
public class GymManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(GymManagementApplication.class, args);
    }
}