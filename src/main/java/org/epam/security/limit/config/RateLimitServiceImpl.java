package org.epam.security.limit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.epam.security.limit.Attempts;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RateLimitServiceImpl implements RateLimitConfig {
    private final ConcurrentHashMap<String, Attempts> attemptsMap = new ConcurrentHashMap<>();

    @Override
    public ConcurrentHashMap<String, Attempts> getAttemptsMap() {
        return attemptsMap;
    }

    @Override
    public String getClientIp(HttpServletRequest request) {
        final List<String> headerNames = List.of(
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        );

        String ip = headerNames.stream()
                .map(request::getHeader)
                .filter(header -> header != null && !header.isEmpty()
                        && !"unknown".equalsIgnoreCase(header))
                .findFirst()
                .orElse(request.getRemoteAddr());

        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip;
    }

    @Override
    public String extractUsernameFromBasicAuth(HttpServletRequest request) {
        var username = "anonymous";

        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            try {
                final String base64Credentials = authHeader.substring("Basic ".length()).trim();
                final byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                final var credentials = new String(credDecoded, StandardCharsets.UTF_8);
                final String[] values = credentials.split(":", 2);
                username = values[0];
                log.debug("Extracted Basic Auth username: {}", username);
            } catch (Exception e) {
                log.debug("Failed to decode Basic Auth header", e);
            }
        }

        return username;
    }

    @Override
    public boolean isRateLimitExceeded(String key, int maxAttempts) {
        log.debug("Rate limit check for key: {}", key);

        final var currentAttempts = attemptsMap.compute(key, (k, attempts) -> {
            long currentTime = System.currentTimeMillis();

            if (attempts == null || attempts.isExpired())
                return new Attempts(1, currentTime, DEFAULT_WINDOW_MILLIS);
            return new Attempts(attempts.count() + 1, attempts.firstAttemptTime(), DEFAULT_WINDOW_MILLIS);
        });

        log.debug("Current attempts for key {}: {}", key, currentAttempts.count());
        return currentAttempts.count() > maxAttempts;
    }

    @Override
    public void sendRateLimitResponse(HttpServletResponse response, String key) throws IOException {
        final var attempts = attemptsMap.get(key);
        final long remainingMillis = attempts.firstAttemptTime() + DEFAULT_WINDOW_MILLIS - System.currentTimeMillis();
        final long remainingMinutes = remainingMillis / (60 * 1000) + 1;

        log.warn("Rate limit exceeded for key: {}. Too many attempts.", key);

        response.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again in "
                + remainingMinutes + " minutes.\"}");
    }
}
