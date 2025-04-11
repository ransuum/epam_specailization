package org.epam.repository;

import org.epam.models.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, String> {
    Optional<Trainer> findByUser_Username(String username);

    void deleteByUser_Username(String username);
}
