package org.epam.utils.querybuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingType;

import java.time.LocalDate;

import static org.epam.utils.CheckerField.check;

public class TrainerTypedQueryBuilder implements TypedQueryBuilder<Training> {
    private final String username;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final String traineeName;
    private final TrainingType trainingType;
    private final EntityManager em;

    public TrainerTypedQueryBuilder(String username, LocalDate fromDate, LocalDate toDate,
                                    String trainerName, TrainingType trainingType, EntityManager em) {
        this.username = username;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.traineeName = trainerName;
        this.trainingType = trainingType;
        this.em = em;
    }

    @Override
    public TypedQuery<Training> createQuery(StringBuilder jpqlBuilder) {
        TypedQuery<Training> query = em.createQuery(jpqlBuilder.toString(), Training.class)
                .setParameter("username", username);
        if (check(fromDate)) query.setParameter("fromDate", fromDate);
        if (check(toDate)) query.setParameter("toDate", toDate);
        if (check(traineeName)) query.setParameter("trainerName", "%" + traineeName + "%");
        if (check(trainingType)) query.setParameter("trainingType", trainingType);
        return query;
    }
}
