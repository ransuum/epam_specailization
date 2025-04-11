package org.epam.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.epam.models.enums.UserType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SecurityContextHolder {
    private String username;
    private String userId;
    private LocalDateTime generateAt;
    private LocalDateTime expiredAt;
    private UserType userType;

    public void initContext(SecurityContextHolder securityContextHolder) {
        this.username = securityContextHolder.getUsername();
        this.userId = securityContextHolder.getUserId();
        this.generateAt = securityContextHolder.getGenerateAt();
        this.expiredAt = securityContextHolder.getExpiredAt();
        this.userType = securityContextHolder.getUserType();
    }

    public void clearContext() {
        this.username = null;
        this.userId = null;
        this.generateAt = null;
        this.expiredAt = null;
        this.userType = UserType.NOT_AUTHORIZE;
    }
}
