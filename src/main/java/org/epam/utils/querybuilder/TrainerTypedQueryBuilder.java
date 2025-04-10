package org.epam.utils.querybuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingTypeName;

import java.time.LocalDate;

import static org.epam.utils.FieldValidator.check;

public class TrainerTypedQueryBuilder implements TypedQueryBuilder<Training> {
    private String username;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String traineeName;
    private TrainingTypeName trainingTypeName;
    private final EntityManager em;

    public TrainerTypedQueryBuilder(EntityManager em) {
        this.em = em;
    }

    public TrainerTypedQueryBuilder username(String username) {
        this.username = username;
        return this;
    }

    public TrainerTypedQueryBuilder fromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public TrainerTypedQueryBuilder toDate(LocalDate toDate) {
        this.toDate = toDate;
        return this;
    }

    public TrainerTypedQueryBuilder traineeName(String trainerName) {
        this.traineeName = trainerName;
        return this;
    }

    public TrainerTypedQueryBuilder trainingTypeName(TrainingTypeName trainingTypeName) {
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
        if (check(traineeName)) {
            query.setParameter("traineeName", "%" + traineeName + "%");
        }
        if (check(trainingTypeName)) {
            query.setParameter("trainingTypeName", trainingTypeName);
        }
        return query;
    }
}
