package org.epam.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Table(name = "training")
@Data
@Builder
@AllArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false, name = "trainee_id")
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false, name = "trainer_id")
    private Trainer trainer;

    @Column(nullable = false, name = "training_name")
    private String trainingName;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false, name = "training_type_id")
    private TrainingType trainingType;

    @Column(nullable = false, name = "start_time")
    private LocalDate startTime;

    @Column(nullable = false)
    private Long duration;
}
