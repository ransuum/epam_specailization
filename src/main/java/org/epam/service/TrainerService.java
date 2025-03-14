package org.epam.service;

import org.epam.models.dto.TrainerDto;
import org.epam.models.entity.Trainer;

import java.util.List;

public interface TrainerService {
    TrainerDto save(Trainer trainer);

    TrainerDto update(Integer id, Trainer trainer);

    void delete(Integer id);

    List<TrainerDto> findAll();

    TrainerDto findById(Integer id);
}
