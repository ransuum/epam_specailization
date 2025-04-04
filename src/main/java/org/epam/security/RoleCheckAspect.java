package org.epam.security;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.epam.exception.PermissionException;
import org.epam.models.SecurityContextHolder;
import org.epam.models.enums.UserType;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleCheckAspect {
    private final SecurityContextHolder securityContextHolder;

    @Around("@annotation(requiredRole)")
    public Object checkUserRole(ProceedingJoinPoint joinPoint, RequiredRole requiredRole) throws Throwable {
        UserType currentUserType = securityContextHolder.getUserType();
        UserType[] requiredRoles = requiredRole.value();
        for (UserType role : requiredRoles)
            if (currentUserType == role) return joinPoint.proceed();

        throw new PermissionException("User does not have any of the required roles");
    }
}
