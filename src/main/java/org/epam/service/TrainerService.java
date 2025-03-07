package org.epam.service;

import org.epam.models.entity.Trainer;

import java.util.List;

public interface TrainerService {
    Trainer save(Trainer t);

    Trainer update(Trainer t);

    void delete(Integer id);

    List<Trainer> findAll();

    Trainer findById(Integer id);
}
