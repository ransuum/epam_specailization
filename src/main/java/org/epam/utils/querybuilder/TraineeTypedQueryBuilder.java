package org.epam.utils.querybuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingTypeName;

import java.time.LocalDate;

import static org.epam.utils.FieldValidator.check;

public class TraineeTypedQueryBuilder implements TypedQueryBuilder<Training> {
    private String username;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String trainerName;
    private TrainingTypeName trainingTypeName;
    private final EntityManager em;

    public TraineeTypedQueryBuilder(EntityManager em) {
        this.em = em;
    }

    public TraineeTypedQueryBuilder username(String username) {
        this.username = username;
        return this;
    }

    public TraineeTypedQueryBuilder fromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public TraineeTypedQueryBuilder toDate(LocalDate toDate) {
        this.toDate = toDate;
        return this;
    }

    public TraineeTypedQueryBuilder trainerName(String trainerName) {
        this.trainerName = trainerName;
        return this;
    }

    public TraineeTypedQueryBuilder trainingTypeName(TrainingTypeName trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
        return this;
    }

    @Override
    public TypedQuery<Training> createQuery(StringBuilder jpqlBuilder) {
        TypedQuery<Training> query = em.createQuery(jpqlBuilder.toString(), Training.class)
                .setParameter("username", username);
        if (check(fromDate)) {
            query.setParameter("fromDate", fromDate);
        }
        if (check(toDate)) {
            query.setParameter("toDate", toDate);
        }
        if (check(trainerName)) {
            query.setParameter("trainerName", "%" + trainerName + "%");
        }
        if (check(trainingTypeName)) {
            query.setParameter("trainingTypeName", trainingTypeName);
        }
        return query;

    }
}
