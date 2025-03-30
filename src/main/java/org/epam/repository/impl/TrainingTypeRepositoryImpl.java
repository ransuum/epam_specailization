package org.epam.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.entity.TrainingType;
import org.epam.models.enums.TrainingTypeName;
import org.epam.repository.TrainingTypeRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.epam.utils.CheckerField.check;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {
    private static final Logger logger = LogManager.getLogger(TrainingTypeRepositoryImpl.class);
    private final EntityManager entityManager;

    public TrainingTypeRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public TrainingType save(TrainingType trainingType) {
        try {
            if (trainingType.getId() == null) entityManager.persist(trainingType);
            else trainingType = entityManager.merge(trainingType);
            return trainingType;
        } catch (Exception e) {
            logger.error("Error in saving trainee: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<TrainingType> findById(String id) {
        return Optional.ofNullable(entityManager.find(TrainingType.class, id));
    }

    @Override
    @Transactional
    public void delete(String id) throws NotFoundException {
        var trainingType = findById(id).orElseThrow(()
                -> new NotFoundException("Not found trainingView by id: " + id));
        if (check(trainingType.getTrainings())) {
            for (Training training : new ArrayList<>(trainingType.getTrainings()))
                entityManager.remove(training);
            trainingType.setTrainings(new ArrayList<>());
        }

        if (check(trainingType.getTrainers())) {
            for (Trainer trainer : new ArrayList<>(trainingType.getTrainers()))
                trainer.setSpecialization(null);
            trainingType.setTrainers(new ArrayList<>());
        }

        entityManager.remove(trainingType);
    }

    @Override
    public List<TrainingType> findAll() {
        return entityManager.createQuery("from TrainingType ", TrainingType.class).getResultList();
    }

    @Override
    @Transactional
    public TrainingType update(String id, TrainingType trainingType) {
        findById(id).ifPresent(trainingViewById -> trainingViewById.setId(id));

        return entityManager.merge(trainingType);
    }

    @Override
    public Optional<TrainingType> findByTrainingTypeName(TrainingTypeName trainingTypeName) {
        try {
            return entityManager.createQuery(
                            "SELECT t FROM TrainingType t WHERE t.trainingTypeName = :trainingTypeName", TrainingType.class)
                    .setParameter("trainingTypeName", trainingTypeName)
                    .getResultStream()
                    .findFirst();
        } catch (Exception e) {
            logger.error("Error finding training type by training type name: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
