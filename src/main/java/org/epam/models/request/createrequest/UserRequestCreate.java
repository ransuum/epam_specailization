package org.epam.models.request.createrequest;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestCreate {
    @NotBlank(message = "firstName is blank")
    private String firstName;

    @NotBlank(message = "firstName is blank")
    private String lastName;

    private Boolean isActive;
}
