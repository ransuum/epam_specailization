package org.epam.service.impl;

import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingViewDto;
import org.epam.models.entity.TrainingView;
import org.epam.models.enums.TrainingType;
import org.epam.repository.TrainingViewRepository;
import org.epam.service.TrainingViewService;
import org.epam.utils.mappers.TrainingViewMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.utils.CheckerField.check;

@Service
public class TrainingViewServiceImpl implements TrainingViewService {
    private final TrainingViewRepository trainingViewRepository;

    public TrainingViewServiceImpl(TrainingViewRepository trainingViewRepository) {
        this.trainingViewRepository = trainingViewRepository;
    }

    @Override
    public TrainingViewDto save(TrainingType trainingType) {
        return TrainingViewMapper.INSTANCE.toDto(trainingViewRepository.save(
                TrainingView.builder()
                        .trainingType(trainingType)
                        .build())
        );
    }

    @Override
    public TrainingViewDto update(String id, TrainingType trainingType) throws NotFoundException {
        var trainingView = trainingViewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Training view not found"));

        if (check(trainingType)) trainingView.setTrainingType(trainingType);
        return TrainingViewMapper.INSTANCE.toDto(trainingViewRepository.update(id, trainingView));
    }

    @Override
    public void delete(String id) throws NotFoundException {
        trainingViewRepository.delete(id);
    }

    @Override
    public List<TrainingViewDto> findAll() throws NotFoundException {
        return trainingViewRepository.findAll()
                .stream()
                .map(TrainingViewMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public TrainingViewDto findById(String id) throws NotFoundException {
        return TrainingViewMapper.INSTANCE.toDto(trainingViewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Training view not found")));
    }
}
