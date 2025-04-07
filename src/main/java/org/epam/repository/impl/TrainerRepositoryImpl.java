package org.epam.repository.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.repository.TrainerRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.epam.utils.FieldValidator.check;

@Repository
@RequiredArgsConstructor
@Log4j2
public class TrainerRepositoryImpl implements TrainerRepository {
    private final EntityManager entityManager;

    @Override
    public Trainer save(Trainer trainer) {
        try {
            if (trainer.getId() == null) entityManager.persist(trainer);
            else trainer = entityManager.merge(trainer);
            return trainer;
        } catch (Exception e) {
            log.error("Error in saving trainee: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<Trainer> findById(String id) {
        return Optional.ofNullable(entityManager.find(Trainer.class, id));
    }

    @Override
    public void delete(String id) {
        Trainer trainer = findById(id).orElseThrow(()
                -> new NotFoundException("Not found trainer by id: " + id));

        if (check(trainer.getTrainings())) {
            for (Training training : new ArrayList<>(trainer.getTrainings()))
                entityManager.remove(training);
            trainer.setTrainings(new ArrayList<>());
        }

        entityManager.remove(trainer);

        entityManager.remove(trainer.getUser());
    }

    @Override
    public List<Trainer> findAll() {
        return entityManager.createQuery("from Trainer", Trainer.class).getResultList();
    }

    @Override
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
            log.error("Error finding trainer by username: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void deleteByUsername(String username) {
        var trainer = findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Not found trainer by name: " + username));
        entityManager.remove(trainer);
        entityManager.remove(trainer.getUser());
    }
}
