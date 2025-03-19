package org.epam.service.impl;

import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainingViewDto;
import org.epam.models.entity.TrainingView;
import org.epam.models.enums.TrainingType;
import org.epam.repository.TrainingViewRepository;
import org.epam.repository.inmemoryrepository.InMemoryTrainingViewRepository;
import org.epam.service.TrainingViewService;
import org.epam.utils.mappers.TrainingViewMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.utils.CheckerField.check;

@Service
public class TrainingViewServiceImpl implements TrainingViewService {
    private final TrainingViewRepository trainingViewRepository;
    private static final Logger log = LogManager.getLogger(TrainingViewServiceImpl.class);

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
    public TrainingViewDto update(String id, TrainingType trainingType) {
        try {
            var trainingView = trainingViewRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Training view not found"));

            if (check(trainingType)) trainingView.setTrainingType(trainingType);
            return TrainingViewMapper.INSTANCE.toDto(trainingViewRepository.update(id, trainingView));
        } catch (NotFoundException e) {
            log.error("Training view not found while updating with this id: {}", id);
            return null;
        }
    }

    @Override
    public void delete(String id) {
        try {
            trainingViewRepository.delete(id);
        } catch (NotFoundException e) {
            log.error("Training view not found while delete with this id: {}", id);
        }
    }

    @Override
    public List<TrainingViewDto> findAll() {
        return trainingViewRepository.findAll()
                .stream()
                .map(TrainingViewMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public TrainingViewDto findById(String id) {
        try {
            return TrainingViewMapper.INSTANCE.toDto(trainingViewRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Training view not found")));
        } catch (NotFoundException e) {
            log.error("Training view not found while finding with this id: {}", id);
            return null;
        }
    }
}
