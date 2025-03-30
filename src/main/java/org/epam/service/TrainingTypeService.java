package org.epam.service;


import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.models.enums.TrainingTypeName;

import java.util.List;

public interface TrainingTypeService {
    TrainingTypeDto save(TrainingTypeName trainingTypeName) ;

    TrainingTypeDto update(String id, TrainingTypeName trainingTypeName) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TrainingTypeDto> findAll();

    TrainingTypeDto findById(String id) throws NotFoundException;
}
