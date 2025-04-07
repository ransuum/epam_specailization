package org.epam.service.impl;


import lombok.RequiredArgsConstructor;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.models.entity.TrainingType;
import org.epam.models.enums.NotFoundMessages;
import org.epam.models.enums.TrainingTypeName;
import org.epam.repository.TrainingTypeRepository;
import org.epam.service.TrainingTypeService;
import org.epam.utils.mappers.TrainingTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.epam.utils.FieldValidator.check;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    @Transactional
    public TrainingTypeDto save(TrainingTypeName trainingTypeName) {
        return TrainingTypeMapper.INSTANCE.toDto(trainingTypeRepository.save(
                TrainingType.builder()
                        .trainingTypeName(trainingTypeName)
                        .build())
        );
    }

    @Override
    @Transactional
    public TrainingTypeDto update(String id, TrainingTypeName trainingTypeName) throws NotFoundException {
        var trainingView = trainingTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINING_TYPE.getVal()));

        if (check(trainingTypeName)) trainingView.setTrainingTypeName(trainingTypeName);
        return TrainingTypeMapper.INSTANCE.toDto(trainingTypeRepository.save(trainingView));
    }

    @Override
    @Transactional
    public void delete(String id) throws NotFoundException {
        trainingTypeRepository.deleteById(id);
    }

    @Override
    public List<TrainingTypeDto> findAll() throws NotFoundException {
        return trainingTypeRepository.findAll()
                .stream()
                .map(TrainingTypeMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    @Transactional
    public TrainingTypeDto findById(String id) throws NotFoundException {
        return TrainingTypeMapper.INSTANCE.toDto(trainingTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundMessages.TRAINING_TYPE.getVal())));
    }
}
