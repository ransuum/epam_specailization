package org.epam.repository;

import org.epam.models.entity.TrainingType;
import org.epam.models.enums.TrainingTypeName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, String> {
    Optional<TrainingType> findByTrainingTypeName(TrainingTypeName trainingTypeName);
}
