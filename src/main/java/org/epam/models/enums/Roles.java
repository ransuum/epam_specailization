package org.epam.models.enums;

import lombok.Getter;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum Roles {
    ROLE_TRAINEE(Set.of("ACC_TRAINEE", "FIND_TRAINEE")),
    ROLE_TRAINER(Set.of("ACC_TRAINER", "FIND_TRAINER"));

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
