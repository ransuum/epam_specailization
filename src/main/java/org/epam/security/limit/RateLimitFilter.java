package org.epam.security.limit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.epam.security.limit.config.RateLimitConfig;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitConfig rateLimitConfig;
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().contains("/sign-in")) {
            final String ipAddress = rateLimitConfig.getClientIp(request);
            final String username = rateLimitConfig.extractUsernameFromBasicAuth(request);
            final String ipKey = "ip_" + ipAddress;
            final String usernameIpKey = "user_" + username + "_" + ipAddress;

            if (rateLimitConfig.isRateLimitExceeded(ipKey, RateLimitConfig.DEFAULT_MAX_ATTEMPTS * 3)) {
                rateLimitConfig.sendRateLimitResponse(response, ipKey);
                return;
            }

            if (rateLimitConfig.isRateLimitExceeded(usernameIpKey, RateLimitConfig.DEFAULT_MAX_ATTEMPTS)) {
                rateLimitConfig.sendRateLimitResponse(response, usernameIpKey);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Scheduled(fixedRate = 60 * 1000)
    public void cleanupOldEntries() {
        rateLimitConfig.getAttemptsMap().entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}
