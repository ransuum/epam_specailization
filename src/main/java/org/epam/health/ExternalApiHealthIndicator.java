package org.epam.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component("externalApi")
public class ExternalApiHealthIndicator implements HealthIndicator {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final Map<String, String> apiEndpoints;

    public ExternalApiHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseUrl = "http://localhost:8000";
        this.apiEndpoints = Map.of(
                "TrainingType API", "/public/training-type/all?page=0&size=10&sort=trainingTypeName%2Casc&forceFirstAndLastRels=true"
        );
    }

    @Override
    public Health health() {
        Health.Builder builder = Health.up();
        boolean allHealthy = true;

        for (Map.Entry<String, String> entry : apiEndpoints.entrySet()) {
            String apiName = entry.getKey();
            String endpoint = entry.getValue();

            try {
                ResponseEntity<String> response = restTemplate
                        .getForEntity(baseUrl + endpoint, String.class);

                if (response.getStatusCode().is2xxSuccessful()) builder.withDetail(apiName, "Available");
                else {
                    builder.withDetail(apiName, "Unavailable - Status: " + response.getStatusCode());
                    allHealthy = false;
                }
            } catch (Exception e) {
                builder.withDetail(apiName, "Down - Error: " + e.getMessage());
                allHealthy = false;
            }
        }
        if (!allHealthy) builder.down();
        return builder.build();
    }
}
