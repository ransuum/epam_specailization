package org.epam.service;

import org.epam.models.dto.TrainingDto;
import org.epam.models.request.TrainingRequest;

import java.util.List;

public interface TrainingService {
    TrainingDto save(TrainingRequest t);

    TrainingDto update(Integer id, TrainingRequest t);

    void delete(Integer id);

    List<TrainingDto> findAll();

    TrainingDto findById(Integer id);
}
