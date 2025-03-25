package org.epam.models.request.update;

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
    private String userId;
    private LocalDate dateOfBirth;
    private String address;
}
