package org.epam.repository;

import org.epam.models.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, String> {
    Optional<Trainer> findByUsers_Username(String username);

    void deleteByUsers_Username(String username);
}
