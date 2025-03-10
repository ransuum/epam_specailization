package org.epam.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.dto.TrainerDto;
import org.epam.models.entity.Trainer;
import org.epam.repository.TrainerRepo;
import org.epam.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.util.CheckerField.check;
import static org.epam.util.sub_controller.SubControllerMenu.existingUsernames;

@Service
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepo trainerRepo;
    private final ObjectMapper objectMapper;
    private static final Log log = LogFactory.getLog(TrainerServiceImpl.class);

    @Autowired
    public TrainerServiceImpl(TrainerRepo trainerRepo, ObjectMapper objectMapper) {
        this.trainerRepo = trainerRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    public TrainerDto save(Trainer t) {
        existingUsernames.add(t.getUsername());
        return objectMapper.convertValue(trainerRepo.save(t), TrainerDto.class);
    }

    @Override
    public TrainerDto update(Integer id, Trainer t) {
        Trainer trainer = trainerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        if (check(t.getFirstName())) trainer.setFirstName(t.getFirstName());
        if (check(t.getLastName())) trainer.setLastName(t.getLastName());
        if (check(t.getUsername())) trainer.setUsername(t.getUsername());
        if (check(t.getPassword())) trainer.setPassword(t.getPassword());
        if (check(t.getSpecialization())) trainer.setSpecialization(t.getSpecialization());
        return objectMapper.convertValue(trainerRepo.update(trainer), TrainerDto.class);
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
                .orElseThrow(() -> new RuntimeException("Trainer not found")), TrainerDto.class);
    }
}
