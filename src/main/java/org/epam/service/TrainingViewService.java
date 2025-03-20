package org.epam.service;


import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingViewDto;
import org.epam.models.enums.TrainingType;

import java.util.List;

public interface TrainingViewService {
    TrainingViewDto save(TrainingType trainingType) ;

    TrainingViewDto update(String id, TrainingType trainingType) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TrainingViewDto> findAll();

    TrainingViewDto findById(String id) throws NotFoundException;
}
