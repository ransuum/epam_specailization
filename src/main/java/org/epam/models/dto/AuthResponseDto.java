package org.epam.models.dto;

public record AuthResponseDto(String username, String password) {
    @Override
    public String toString() {
        return "AuthDto[username=" + username + ", password=***]";
    }
}
