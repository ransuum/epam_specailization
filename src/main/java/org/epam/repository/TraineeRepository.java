package org.epam.repository;

import org.epam.models.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, String> {
    Optional<Trainee> findByUser_Username(String username);
    void deleteByUser_Username(String username);
}
