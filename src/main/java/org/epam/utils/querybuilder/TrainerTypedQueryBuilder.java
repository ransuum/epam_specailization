package org.epam.utils.querybuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingTypeName;

import java.time.LocalDate;

import static org.epam.utils.CheckerField.check;

public class TrainerTypedQueryBuilder implements TypedQueryBuilder<Training> {
    private final String username;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final String traineeName;
    private final TrainingTypeName trainingTypeName;
    private final EntityManager em;

    public TrainerTypedQueryBuilder(String username, LocalDate fromDate, LocalDate toDate,
                                    String trainerName, TrainingTypeName trainingTypeName, EntityManager em) {
        this.username = username;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.traineeName = trainerName;
        this.trainingTypeName = trainingTypeName;
        this.em = em;
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
            query.setParameter("trainingName", trainingTypeName);
        }
        return query;

    }
}
