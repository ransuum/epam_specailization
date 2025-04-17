package org.epam.security.limit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.epam.security.limit.Attempts;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public interface RateLimitConfig {
    String getClientIp(HttpServletRequest request);

    String extractUsernameFromBasicAuth(HttpServletRequest request);

    boolean isRateLimitExceeded(String key, int maxAttempts);

    void sendRateLimitResponse(HttpServletResponse response, String key) throws IOException;

    ConcurrentHashMap<String, Attempts> getAttemptsMap();

    int DEFAULT_MAX_ATTEMPTS = 3;
    int DEFAULT_WINDOW_MINUTES = 5;
    long DEFAULT_WINDOW_MILLIS = DEFAULT_WINDOW_MINUTES * 60 * 1000L;
}
