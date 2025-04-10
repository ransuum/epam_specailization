package org.epam.repository;

import org.epam.models.entity.Training;
import org.epam.models.enums.TrainingTypeName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, String> {

    @Query("SELECT t FROM Training t " +
            "WHERE t.trainee.user.username = :username " +
            "AND (:fromDate IS NULL OR t.startTime >= :fromDate) " +
            "AND (:toDate IS NULL OR t.startTime <= :toDate) " +
            "AND (:trainerName IS NULL OR t.trainer.user.firstName = :trainerName) " +
            "AND (:trainingType IS NULL OR t.trainingType.trainingTypeName = :trainingType)")
    Page<Training> getTraineeTrainings(@Param("username") String username,
                                       @Param("fromDate") LocalDate fromDate,
                                       @Param("toDate") LocalDate toDate,
                                       @Param("trainerName") String trainerName,
                                       @Param("trainingType") TrainingTypeName trainingType,
                                       Pageable pageable);

    @Query("SELECT t FROM Training t " +
            "WHERE t.trainer.user.username = :username " +
            "AND (:fromDate IS NULL OR t.startTime >= :fromDate) " +
            "AND (:toDate IS NULL OR t.startTime <= :toDate) " +
            "AND (:traineeName IS NULL OR t.trainee.user.firstName = :trainerName) " +
            "AND (:trainingType IS NULL OR t.trainingType.trainingTypeName = :trainingType)")
    Page<Training> getTrainerTrainings(@Param("username") String username,
                                       @Param("fromDate") LocalDate fromDate,
                                       @Param("toDate") LocalDate toDate,
                                       @Param("traineeName") String traineeName,
                                       @Param("trainingType") TrainingTypeName trainingType,
                                       Pageable pageable);
}
