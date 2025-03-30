package org.epam.service.impl;

import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.models.entity.TrainingType;
import org.epam.models.enums.TrainingTypeName;
import org.epam.repository.TrainingTypeRepository;
import org.epam.service.TrainingTypeService;
import org.epam.utils.mappers.TrainingTypeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.utils.CheckerField.check;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {
    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    public TrainingTypeDto save(TrainingTypeName trainingTypeName) {
        return TrainingTypeMapper.INSTANCE.toDto(trainingTypeRepository.save(
                TrainingType.builder()
                        .trainingTypeName(trainingTypeName)
                        .build())
        );
    }

    @Override
    public TrainingTypeDto update(String id, TrainingTypeName trainingTypeName) throws NotFoundException {
        var trainingView = trainingTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Training type not found"));

        if (check(trainingTypeName)) trainingView.setTrainingTypeName(trainingTypeName);
        return TrainingTypeMapper.INSTANCE.toDto(trainingTypeRepository.update(id, trainingView));
    }

    @Override
    public void delete(String id) throws NotFoundException {
        trainingTypeRepository.delete(id);
    }

    @Override
    public List<TrainingTypeDto> findAll() throws NotFoundException {
        return trainingTypeRepository.findAll()
                .stream()
                .map(TrainingTypeMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public TrainingTypeDto findById(String id) throws NotFoundException {
        return TrainingTypeMapper.INSTANCE.toDto(trainingTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Training type not found")));
    }
}
