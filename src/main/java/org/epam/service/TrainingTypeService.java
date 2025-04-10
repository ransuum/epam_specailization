package org.epam.service;

import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.models.enums.TrainingTypeName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrainingTypeService {
    TrainingTypeDto save(TrainingTypeName trainingTypeName) ;

    TrainingTypeDto update(String id, TrainingTypeName trainingTypeName) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    Page<TrainingTypeDto> findAll(Pageable pageable);

    TrainingTypeDto findById(String id) throws NotFoundException;
}
