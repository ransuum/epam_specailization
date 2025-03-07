package org.epam.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.entity.Trainee;
import org.epam.repository.TraineeRepo;
import org.epam.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepo traineeRepo;
    private static final Log log = LogFactory.getLog(TraineeServiceImpl.class);

    @Autowired
    public TraineeServiceImpl(TraineeRepo traineeRepo) {
        this.traineeRepo = traineeRepo;
    }

    @Override
    public Trainee save(Trainee t) {
        log.info("Saving Trainee...");
        return traineeRepo.save(t);
    }

    @Override
    public Trainee update(Trainee t) {
        log.info("update Trainee...");
        Trainee trainee = traineeRepo.findById(t.getId())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
        if (!t.getAddress().isEmpty()) trainee.setAddress(t.getAddress());
        if (!t.getPassword().trim().isEmpty()) trainee.setPassword(t.getPassword());
        if (!t.getUsername().trim().isEmpty()) trainee.setUsername(t.getUsername());
        if (!t.getFirstName().trim().isEmpty()) trainee.setFirstName(t.getFirstName());
        if (!t.getLastName().trim().isEmpty()) trainee.setLastName(t.getLastName());
        if (t.getDateOfBirth() != null) trainee.setDateOfBirth(t.getDateOfBirth());
        return traineeRepo.update(trainee);
    }

    @Override
    public void delete(Integer id) {
        log.info("delete Trainee...");
        traineeRepo.delete(id);
    }

    @Override
    public List<Trainee> findAll() {
        return traineeRepo.findAll();
    }

    @Override
    public Trainee findById(Integer id) {
        return traineeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
    }
}
