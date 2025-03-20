package org.epam.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.Trainer;
import org.epam.repository.TrainerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
    private static final Logger logger = LogManager.getLogger(TrainerRepositoryImpl.class);

    private final EntityManager entityManager;

    public TrainerRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Trainer save(Trainer trainer) {
        try {
            if (trainer.getId() == null) entityManager.persist(trainer);
            else trainer = entityManager.merge(trainer);
            return trainer;
        } catch (Exception e) {
            logger.error("Error in saving trainee: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<Trainer> findById(String id) {
        return Optional.ofNullable(entityManager.find(Trainer.class, id));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Trainer trainer = findById(id).orElseThrow(()
                -> new NotFoundException("Not found trainer by id: " + id));
        if (entityManager.contains(trainer)) entityManager.remove(trainer);
        else entityManager.merge(trainer);
    }

    @Override
    public List<Trainer> findAll() {
        return entityManager.createQuery("from Trainer", Trainer.class).getResultList();
    }

    @Override
    @Transactional
    public Trainer update(String id, Trainer trainer) {
        findById(id).ifPresent(trainerById -> trainer.setId(id));

        return entityManager.merge(trainer);
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        try {
            return entityManager.createQuery(
                            "SELECT t FROM Trainer t JOIN t.user u WHERE u.username = :username", Trainer.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();
        } catch (Exception e) {
            logger.error("Error finding trainer by username: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
