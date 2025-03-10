package org.epam.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.exception.EntityNotFoundException;
import org.epam.models.dto.TrainerDto;
import org.epam.models.entity.Trainer;
import org.epam.repository.TrainerRepo;
import org.epam.service.TrainerService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.util.CheckerField.check;
import static org.epam.util.subcontroller.SubControllerMenu.existingUsernames;

@Service
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepo trainerRepo;
    private final ObjectMapper objectMapper;
    private static final Log log = LogFactory.getLog(TrainerServiceImpl.class);

    public TrainerServiceImpl(TrainerRepo trainerRepo, ObjectMapper objectMapper) {
        this.trainerRepo = trainerRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    public TrainerDto save(Trainer trainer) {
        existingUsernames.add(trainer.getUsername());
        return objectMapper.convertValue(trainerRepo.save(trainer), TrainerDto.class);
    }

    @Override
    public TrainerDto update(Integer id, Trainer trainer) {
        Trainer trainerById = trainerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));

        if (check(trainer.getFirstName())) trainerById.setFirstName(trainer.getFirstName());
        if (check(trainer.getLastName())) trainerById.setLastName(trainer.getLastName());
        if (check(trainer.getUsername())) trainerById.setUsername(trainer.getUsername());
        if (check(trainer.getPassword())) trainerById.setPassword(trainer.getPassword());
        if (check(trainer.getSpecialization())) trainerById.setSpecialization(trainer.getSpecialization());
        return objectMapper.convertValue(trainerRepo.update(trainerById), TrainerDto.class);
    }

    @Override
    public void delete(Integer id) {
        existingUsernames.remove(findById(id).username());
        trainerRepo.delete(id);
    }

    @Override
    public List<TrainerDto> findAll() {
        return trainerRepo.findAll()
                .stream()
                .map(trainer ->
                        objectMapper.convertValue(trainer, TrainerDto.class))
                .toList();
    }

    @Override
    public TrainerDto findById(Integer id) {
        return objectMapper.convertValue(trainerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found")), TrainerDto.class);
    }
}
