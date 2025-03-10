package org.epam.service;

import org.epam.models.dto.TrainerDto;
import org.epam.models.entity.Trainer;

import java.util.List;

public interface TrainerService {
    TrainerDto save(Trainer t);

    TrainerDto update(Integer id, Trainer t);

    void delete(Integer id);

    List<TrainerDto> findAll();

    TrainerDto findById(Integer id);
}
