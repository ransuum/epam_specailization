package org.epam.service;


import org.epam.models.dto.TrainingViewDto;
import org.epam.models.enums.TrainingType;

import java.util.List;

public interface TrainingViewService {
    TrainingViewDto save(TrainingType trainingType);

    TrainingViewDto update(String id, TrainingType trainingType);

    void delete(String id);

    List<TrainingViewDto> findAll();

    TrainingViewDto findById(String id);
}
