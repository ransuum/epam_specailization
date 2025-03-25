package org.epam.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.Trainee;
import org.epam.repository.TraineeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {
    private static final Logger logger = LogManager.getLogger(TraineeRepositoryImpl.class);

    private final EntityManager entityManager;

    public TraineeRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Trainee save(Trainee trainee) {
        try {
            if (trainee.getId() == null) entityManager.persist(trainee);
            else trainee = entityManager.merge(trainee);
            return trainee;
        } catch (Exception e) {
            logger.error("Error in saving trainee: {}", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public Trainee update(String id, Trainee trainee) {
        findById(id).ifPresent(traineeById -> trainee.setId(id));
        return entityManager.merge(trainee);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        try {
            return entityManager.createQuery(
                            "SELECT t FROM Trainee t JOIN t.user u WHERE u.username = :username", Trainee.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();
        } catch (Exception e) {
            logger.error("Error finding trainer by username: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public String deleteByUsername(String username) throws NotFoundException {
        var trainee = findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));
        entityManager.remove(trainee);
        entityManager.remove(trainee.getUser());
        return username;
    }

    @Override
    public Optional<Trainee> findById(String id) {
        return Optional.ofNullable(entityManager.find(Trainee.class, id));
    }


    @Override
    public void delete(String id) throws NotFoundException {
        var trainee = findById(id).orElseThrow(()
                -> new NotFoundException("Not found trainee by id: " + id));
        if (entityManager.contains(trainee)) {
            entityManager.remove(trainee);
            entityManager.remove(trainee.getUser());
        }
        else entityManager.merge(trainee);
    }

    @Override
    public List<Trainee> findAll() {
        return entityManager.createQuery("from Trainee", Trainee.class).getResultList();
    }
}
