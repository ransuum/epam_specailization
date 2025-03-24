package org.epam.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.epam.models.enums.TrainingName;

import java.util.List;

@NoArgsConstructor
@Entity
@Table(name = "training_type")
@Data
@Builder
@AllArgsConstructor
public class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "training_name")
    private TrainingName trainingName;

    @OneToMany(mappedBy = "trainingType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Training> trainings;

    @OneToMany(mappedBy = "specialization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Trainer> trainers;
}
