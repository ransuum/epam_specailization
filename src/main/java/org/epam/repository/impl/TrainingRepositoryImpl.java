package org.epam.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingName;
import org.epam.repository.TrainingRepository;
import org.epam.utils.querybuilder.TraineeTypedQueryBuilder;
import org.epam.utils.querybuilder.TrainerTypedQueryBuilder;
import org.epam.utils.querybuilder.TypedQueryBuilder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.epam.utils.CheckerField.check;


@Repository
public class TrainingRepositoryImpl implements TrainingRepository {
    private static final Logger logger = LogManager.getLogger(TrainingRepositoryImpl.class);

    private final EntityManager entityManager;

    private TypedQueryBuilder<Training> trainingTypedQueryBuilder;

    public TrainingRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Training save(Training training) {
        try {
            if (training.getId() == null) entityManager.persist(training);
            else training = entityManager.merge(training);
            return training;
        } catch (Exception e) {
            logger.error("Error in saving trainee: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<Training> findById(String id) {
        return Optional.ofNullable(entityManager.find(Training.class, id));
    }

    @Override
    @Transactional
    public void delete(String id) throws NotFoundException {
        Training training = findById(id).orElseThrow(()
                -> new NotFoundException("Not found trainee by id: " + id));
        if (entityManager.contains(training)) entityManager.remove(training);
        else entityManager.merge(training);
    }

    @Override
    public List<Training> findAll() {
        return entityManager.createQuery("from Training", Training.class).getResultList();
    }

    @Override
    @Transactional
    public Training update(String id, Training training) {
        findById(id).ifPresent(trainingById -> training.setId(id));
        return entityManager.merge(training);
    }

    @Override
    public List<Training> findTrainingWithUsernameOfTrainee(String username, LocalDate fromDate,
                                                            LocalDate toDate, String trainerName,
                                                            TrainingName trainingName) {
        try {
            StringBuilder jpqlBuilder = new StringBuilder(
                    "SELECT t FROM Training t JOIN t.trainee tr JOIN tr.user u JOIN t.trainer tn JOIN tn.user tu");

            if (check(trainingName)) jpqlBuilder.append(" JOIN t.trainingView tv");

            jpqlBuilder.append(" WHERE u.username = :username");

            if (check(fromDate)) jpqlBuilder.append(" AND t.startTime >= :fromDate");

            if (check(toDate)) jpqlBuilder.append(" AND t.startTime <= :toDate");

            if (check(trainerName)) jpqlBuilder.append(" AND tu.firstName LIKE :trainerName");

            if (check(trainingName)) jpqlBuilder.append(" AND tv.trainingType = :trainingType");

            trainingTypedQueryBuilder = new TraineeTypedQueryBuilder(username, fromDate, toDate, trainerName, trainingName, entityManager);
            return trainingTypedQueryBuilder.createQuery(jpqlBuilder).getResultList();
        } catch (Exception e) {
            logger.error("Error in finding trainings by trainee username: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<Training> findTrainingWithUsernameOfTrainer(String username, LocalDate fromDate, LocalDate toDate, String traineeName, TrainingName trainingName) {
        try {
            StringBuilder jpqlBuilder = new StringBuilder(
                    "SELECT t FROM Training t JOIN t.trainer tr JOIN tr.user u JOIN t.trainee tn JOIN tn.user tu");

            if (check(trainingName)) jpqlBuilder.append(" JOIN t.trainingView tv");

            jpqlBuilder.append(" WHERE u.username = :username");

            if (check(fromDate)) jpqlBuilder.append(" AND t.startTime >= :fromDate");

            if (check(toDate)) jpqlBuilder.append(" AND t.startTime <= :toDate");

            if (check(traineeName)) jpqlBuilder.append(" AND tu.firstName LIKE :trainerName");

            if (check(trainingName)) jpqlBuilder.append(" AND tv.trainingType = :trainingType");

            trainingTypedQueryBuilder = new TrainerTypedQueryBuilder(username, fromDate, toDate, traineeName, trainingName, entityManager);
            return trainingTypedQueryBuilder.createQuery(jpqlBuilder).getResultList();
        } catch (Exception e) {
            logger.error("Error in finding trainings by trainer username: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
