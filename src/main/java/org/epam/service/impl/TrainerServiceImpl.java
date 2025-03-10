package org.epam.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.entity.Trainer;
import org.epam.repository.TrainerRepo;
import org.epam.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.util.sub_controller.SubControllerMenu.existingUsernames;

@Service
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepo trainerRepo;
    private static final Log log = LogFactory.getLog(TrainerServiceImpl.class);

    @Autowired
    public TrainerServiceImpl(TrainerRepo trainerRepo) {
        this.trainerRepo = trainerRepo;
    }

    @Override
    public Trainer save(Trainer t) {
        existingUsernames.add(t.getUsername());
        return trainerRepo.save(t);
    }

    @Override
    public Trainer update(Trainer t) {
        Trainer trainer = findById(t.getId());
        if (!t.getFirstName().trim().isEmpty()) trainer.setFirstName(t.getFirstName());
        if (!t.getLastName().trim().isEmpty()) trainer.setLastName(t.getLastName());
        if (!t.getUsername().trim().isEmpty()) trainer.setUsername(t.getUsername());
        if (!t.getPassword().trim().isEmpty()) trainer.setPassword(t.getPassword());
        return trainerRepo.update(trainer);
    }

    @Override
    public void delete(Integer id) {
        existingUsernames.remove(findById(id).getUsername());
        trainerRepo.delete(id);
    }

    @Override
    public List<Trainer> findAll() {
        return trainerRepo.findAll();
    }

    @Override
    public Trainer findById(Integer id) {
        return trainerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
    }
}
