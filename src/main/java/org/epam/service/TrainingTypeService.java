package org.epam.service;


import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.models.enums.TrainingName;

import java.util.List;

public interface TrainingTypeService {
    TrainingTypeDto save(TrainingName trainingName) ;

    TrainingTypeDto update(String id, TrainingName trainingName) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TrainingTypeDto> findAll();

    TrainingTypeDto findById(String id) throws NotFoundException;
}
