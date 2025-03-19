package org.epam.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.epam.models.enums.TrainingType;

import java.util.List;

@NoArgsConstructor
@Entity
@Table(name = "training_view")
@Data
@Builder
@AllArgsConstructor
public class TrainingView {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "training_type")
    private TrainingType trainingType;

    @OneToMany(mappedBy = "trainingView", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Training> trainings;

    public TrainingView(TrainingType trainingType) {
        this.trainingType = trainingType;
    }
}
