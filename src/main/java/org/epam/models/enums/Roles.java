package org.epam.models.enums;

import lombok.Getter;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum Roles {
    ROLE_TRAINEE(Set.of("VIEW_TRAINEE_PROFILE", "SEARCH_TRAINEES", "AUTHORIZED")),
    ROLE_TRAINER(Set.of("VIEW_TRAINER_PROFILE", "SEARCH_TRAINERS", "CHANGE_STATUS", "AUTHORIZED")),
    ADMIN(Set.of("TRAINEE_DELETE", "TRAINER_DELETE", "FULL_ACCESS"));

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
