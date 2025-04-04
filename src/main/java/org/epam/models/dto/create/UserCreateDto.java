package org.epam.models.dto.create;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateDto {
    @NotBlank(message = "firstName is blank")
    private String firstName;

    @NotBlank(message = "firstName is blank")
    private String lastName;

    private Boolean isActive;
}
