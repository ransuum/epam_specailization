package org.epam.repository.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Training;
import org.epam.repository.TraineeRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.epam.utils.CheckerBuilder.check;

@Repository
@RequiredArgsConstructor
@Log4j2
public class TraineeRepositoryImpl implements TraineeRepository {

    private final EntityManager entityManager;

    @Override
    public Trainee save(Trainee trainee) {
        try {
            if (trainee.getId() == null) entityManager.persist(trainee);
            else trainee = entityManager.merge(trainee);
            return trainee;
        } catch (Exception e) {
            log.error("Error in saving trainee: {}", e.getMessage());
            return null;
        }

    }

    @Override
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
            log.error("Error finding trainer by username: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public String deleteByUsername(String username) throws NotFoundException {
        var trainee = findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found"));
        if (check(trainee.getTrainings())) {
            for (Training training : new ArrayList<>(trainee.getTrainings()))
                entityManager.remove(training);
            trainee.setTrainings(new ArrayList<>());
        }

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
        if (check(trainee.getTrainings())) {
            for (Training training : new ArrayList<>(trainee.getTrainings()))
                entityManager.remove(training);
            trainee.setTrainings(new ArrayList<>());
        }

        entityManager.remove(trainee);

        entityManager.remove(trainee.getUser());
    }

    @Override
    public List<Trainee> findAll() {
        return entityManager.createQuery("from Trainee", Trainee.class).getResultList();
    }
}
