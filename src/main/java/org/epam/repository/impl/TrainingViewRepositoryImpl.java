package org.epam.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.TrainingView;
import org.epam.repository.TrainingViewRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingViewRepositoryImpl implements TrainingViewRepository {
    private static final Logger logger = LogManager.getLogger(TrainingViewRepositoryImpl.class);
    private final EntityManager entityManager;

    public TrainingViewRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public TrainingView save(TrainingView trainingView) {
        try {
            if (trainingView.getId() == null) entityManager.persist(trainingView);
            else trainingView = entityManager.merge(trainingView);
            return trainingView;
        } catch (Exception e) {
            logger.error("Error in saving trainee: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<TrainingView> findById(String id) {
        return Optional.ofNullable(entityManager.find(TrainingView.class, id));
    }

    @Override
    @Transactional
    public void delete(String id) throws NotFoundException {
        var trainingView = findById(id).orElseThrow(()
                -> new NotFoundException("Not found trainingView by id: " + id));
        if (entityManager.contains(trainingView)) entityManager.remove(trainingView);
        else entityManager.merge(trainingView);
    }

    @Override
    public List<TrainingView> findAll() {
        return entityManager.createQuery("from TrainingView ", TrainingView.class).getResultList();
    }

    @Override
    @Transactional
    public TrainingView update(String id, TrainingView trainingView) {
        findById(id).ifPresent(trainingViewById -> trainingViewById.setId(id));

        return entityManager.merge(trainingView);
    }
}
