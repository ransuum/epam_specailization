package org.epam.security.limit;

public record Attempts(int count, long firstAttemptTime, long timeWindowMillis) {
    public boolean isExpired() {
        return (System.currentTimeMillis() - firstAttemptTime) > timeWindowMillis;
    }
}
