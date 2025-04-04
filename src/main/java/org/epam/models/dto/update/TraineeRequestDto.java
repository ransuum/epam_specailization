package org.epam.models.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TraineeRequestDto {
    private String dateOfBirth;
    private String address;

    @NotBlank(message = "firstname is blank")
    private String firstname;
    @NotBlank(message = "lastname is blank")
    private String lastname;
    @NotBlank(message = "username is blank")
    private String username;
    @NotNull(message = "isActive must not be null")
    private Boolean isActive;
}
