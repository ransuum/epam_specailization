package org.epam.utils.querybuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingName;

import java.time.LocalDate;

import static org.epam.utils.CheckerField.check;

public class TraineeTypedQueryBuilder implements TypedQueryBuilder<Training> {
    private final String username;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final String trainerName;
    private final TrainingName trainingName;
    private final EntityManager em;

    public TraineeTypedQueryBuilder(String username, LocalDate fromDate, LocalDate toDate,
                                    String trainerName, TrainingName trainingName, EntityManager em) {
        this.username = username;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.trainerName = trainerName;
        this.trainingName = trainingName;
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
        if (check(trainerName)) {
            query.setParameter("trainerName", "%" + trainerName + "%");
        }
        if (check(trainingName)) {
            query.setParameter("trainingName", trainingName);
        }
        return query;

    }
}
