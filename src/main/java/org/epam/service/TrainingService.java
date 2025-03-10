package org.epam.service;

import org.epam.models.entity.Training;
import org.epam.models.request.TrainingRequest;

import java.util.List;

public interface TrainingService {
    Training save(TrainingRequest t);

    Training update(Integer id, TrainingRequest t);

    void delete(Integer id);

    List<Training> findAll();

    Training findById(Integer id);
}
