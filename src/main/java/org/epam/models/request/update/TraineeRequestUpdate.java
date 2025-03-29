package org.epam.models.request.update;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TraineeRequestUpdate {
    private String dateOfBirth;
    private String address;

    @NotBlank(message = "firstname is blank")
    private String firstname;
    @NotBlank(message = "firstname is blank")
    private String lastname;
    @NotBlank(message = "firstname is blank")
    private String username;
    @NotBlank(message = "firstname is blank")
    private Boolean isActive;
}
