package org.epam.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

//@Component("externalApi")
//public class ExternalApiHealthIndicator implements HealthIndicator {
//    private final RestTemplate restTemplate;
//    private final String baseUrl;
//    private final Map<String, String> apiEndpoints;
//    private final SecurityContextHolder securityContextHolder;
//
//    public ExternalApiHealthIndicator(RestTemplate restTemplate, SecurityContextHolder securityContextHolder) {
//        this.restTemplate = restTemplate;
//        this.securityContextHolder = securityContextHolder;
//        this.baseUrl = "http://localhost:8000/";
//        this.apiEndpoints = Map.of(
//                "Trainee API", "/trainee",
//                "Trainer API", "/trainer",
//                "Training API", "/training",
//                "TrainingType API", "/training-type",
//                "User API", "/users"
//        );
//    }
//
//    @Override
//    public Health health() {
//        securityContextHolder.initContext(SecurityContextHolder.builder()
//                .userType(UserType.ADMIN)
//                .expiredAt(LocalDateTime.MAX)
//                .generateAt(LocalDateTime.now())
//                .userId("admin")
//                .username("admin")
//                .build());
//        Health.Builder builder = Health.up();
//        boolean allHealthy = true;
//
//        for (Map.Entry<String, String> entry : apiEndpoints.entrySet()) {
//            String apiName = entry.getKey();
//            String endpoint = entry.getValue();
//
//            try {
//                ResponseEntity<String> response = restTemplate
//                        .getForEntity(baseUrl + endpoint, String.class);
//
//                if (response.getStatusCode().is2xxSuccessful()) builder.withDetail(apiName, "Available");
//                else {
//                    builder.withDetail(apiName, "Unavailable - Status: " + response.getStatusCode());
//                    allHealthy = false;
//                }
//            } catch (Exception e) {
//                builder.withDetail(apiName, "Down - Error: " + e.getMessage());
//                allHealthy = false;
//            }
//        }
//        if (!allHealthy) builder.down();
//
//
//        securityContextHolder.clearContext();
//        return builder.build();
//    }
//}
