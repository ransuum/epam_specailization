package org.epam.models.enums;

import lombok.Getter;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum Roles {
    ROLE_TRAINEE(Set.of("TRAINEE_PROFILE", "READ_TRAINEES", "AUTHORIZED")),
    ROLE_TRAINER(Set.of("TRAINER_PROFILE", "READ_TRAINERS", "AUTHORIZED")),
    ROLE_ADMIN(Set.of("DELETE", "FULL_ACCESS", "TRAINER_PROFILE", "TRAINEE_PROFILE", "READ_TRAINERS", "AUTHORIZED", "READ_TRAINEES"));

    private final Set<String> permissions;

    Roles(Set<String> permissions) {
        this.permissions = permissions;
    }

    public static Set<String> getPermissionsForRoles(Collection<String> roles) {
        return roles.stream()
                .map(Roles::valueOf)
                .flatMap(applicationRole -> applicationRole.getPermissions().stream())
                .collect(Collectors.toSet());
    }
}
