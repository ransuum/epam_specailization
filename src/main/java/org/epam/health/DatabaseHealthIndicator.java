package org.epam.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component("database")
@Slf4j
public class DatabaseHealthIndicator implements HealthIndicator {
    private final JdbcTemplate jdbcTemplate;
    private String databaseName;

    public DatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            this.databaseName = connection.getSchema();
        } catch (SQLException e) {
            log.info("Database connection could not be established due to: {}", e.getMessage());
        }
    }

    @Override
    public Health health() {
        try {
            var result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result != null && result == 1) {
                Map<String, Object> entityCounts = new HashMap<>();

                entityCounts.put("users", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class));
                entityCounts.put("trainee", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM trainee", Long.class));
                entityCounts.put("trainer", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM trainer", Long.class));
                entityCounts.put("training", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM training", Long.class));
                entityCounts.put("training_type", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM training_type", Long.class));

                entityCounts.put("active_users", jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM users WHERE is_active = true", Long.class));

                entityCounts.put("avg_trainings_per_trainee", jdbcTemplate.queryForObject(
                        "SELECT COALESCE(AVG(training_count), 0) FROM " +
                                "(SELECT COUNT(*) as training_count FROM training GROUP BY trainee_id) as counts",
                        Double.class));

                return Health.up()
                        .withDetail("database", databaseName)
                        .withDetail("status", "Connected")
                        .withDetail("entities", entityCounts)
                        .withDetail("version", jdbcTemplate.queryForObject("SELECT version()", String.class))
                        .build();
            } else return Health.down()
                    .withDetail("database", databaseName)
                    .withDetail("error", "Failed to execute test query")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", databaseName)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
