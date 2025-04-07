package org.epam.repository.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingTypeName;
import org.epam.repository.TrainingRepository;
import org.epam.utils.querybuilder.TraineeTypedQueryBuilder;
import org.epam.utils.querybuilder.TrainerTypedQueryBuilder;
import org.epam.utils.querybuilder.TypedQueryBuilder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.epam.utils.FieldValidator.check;

@Repository
@RequiredArgsConstructor
@Log4j2
public class TrainingRepositoryImpl implements TrainingRepository {
    private final EntityManager entityManager;

    @Override
    public Training save(Training training) {
        try {
            if (training.getId() == null) entityManager.persist(training);
            else training = entityManager.merge(training);
            return training;
        } catch (Exception e) {
            log.error("Error in saving trainee: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<Training> findById(String id) {
        return Optional.ofNullable(entityManager.find(Training.class, id));
    }

    @Override
    public void delete(String id) throws NotFoundException {
        var training = findById(id).orElseThrow(()
                -> new NotFoundException("Not found trainee by id: " + id));
        var trainer = training.getTrainer();
        if (trainer != null && check(trainer.getTrainings()))
            trainer.getTrainings().remove(training);

        var trainee = training.getTrainee();
        if (trainee != null && check(trainee.getTrainings()))
            trainee.getTrainings().remove(training);

        entityManager.remove(training);
    }

    @Override
    public List<Training> findAll() {
        return entityManager.createQuery("from Training", Training.class).getResultList();
    }

    @Override
    public Training update(String id, Training training) {
        findById(id).ifPresent(trainingById -> training.setId(id));
        return entityManager.merge(training);
    }

    @Override
    public List<Training> getTraineeTrainings(String username, LocalDate fromDate,
                                              LocalDate toDate, String trainerName,
                                              TrainingTypeName trainingTypeName) {
        try {
            StringBuilder jpqlBuilder = new StringBuilder(
                    "SELECT t FROM Training t JOIN t.trainee tr JOIN tr.user u " +
                            "JOIN t.trainer tn JOIN tn.user tu");

            if (check(trainingTypeName))
                jpqlBuilder.append(" JOIN t.trainingType tv");

            jpqlBuilder.append(" WHERE u.username = :username");

            if (check(fromDate))
                jpqlBuilder.append(" AND t.startTime >= :fromDate");

            if (check(toDate))
                jpqlBuilder.append(" AND t.startTime <= :toDate");

            if (check(trainerName))
                jpqlBuilder.append(" AND tu.firstName LIKE :trainerName");

            if (check(trainingTypeName))
                jpqlBuilder.append(" AND tv.trainingTypeName = :trainingTypeName");

            return new TraineeTypedQueryBuilder(entityManager)
                    .fromDate(fromDate)
                    .toDate(toDate)
                    .trainingTypeName(trainingTypeName)
                    .username(username)
                    .trainerName(trainerName)
                    .createQuery(jpqlBuilder).getResultList();
        } catch (Exception e) {
            log.error("Error in finding trainings by trainee username: {}", e.getMessage());
            return Collections.emptyList();
        }

    }

    @Override
    public List<Training> getTrainerTrainings(String username, LocalDate fromDate,
                                              LocalDate toDate, String traineeName,
                                              TrainingTypeName trainingTypeName) {
        try {
            StringBuilder jpqlBuilder = new StringBuilder(
                    "SELECT t FROM Training t JOIN t.trainer tr JOIN tr.user u " +
                            "JOIN t.trainee tn JOIN tn.user tu");

            if (check(trainingTypeName))
                jpqlBuilder.append(" JOIN t.trainingType tv");

            jpqlBuilder.append(" WHERE u.username = :username");

            if (check(fromDate))
                jpqlBuilder.append(" AND t.startTime >= :fromDate");

            if (check(toDate))
                jpqlBuilder.append(" AND t.startTime <= :toDate");

            if (check(traineeName))
                jpqlBuilder.append(" AND tu.firstName LIKE :traineeName");

            if (check(trainingTypeName))
                jpqlBuilder.append(" AND tv.trainingTypeName = :trainingTypeName");

            return new TrainerTypedQueryBuilder(entityManager)
                    .fromDate(fromDate)
                    .traineeName(traineeName)
                    .username(username)
                    .toDate(toDate)
                    .trainingTypeName(trainingTypeName)
                    .createQuery(jpqlBuilder).getResultList();
        } catch (Exception e) {
            log.error("Error in finding trainings by trainer username: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
