package org.epam.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthResponseDto(@JsonProperty("access_token") String accessToken,
                              @JsonProperty("access_token_expiry") int accessTokenExpiry,
                              @JsonProperty("token_type") String tokenType,
                              @JsonProperty("user_name") String username,
                              @JsonProperty("refresh_token") String refreshToken) {
}
