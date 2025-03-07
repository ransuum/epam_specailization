package org.epam.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.epam.models.enums.TrainingType;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingRequest {
        private Integer id;
        private Integer traineeId;
        private Integer trainerId;
        private String trainingName;
        private TrainingType trainingType;
        private LocalDate trainingDate;
        private Integer trainingDuration;
}
