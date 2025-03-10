package org.epam.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.models.dto.TraineeDto;
import org.epam.models.entity.Trainee;
import org.epam.repository.TraineeRepo;
import org.epam.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.util.CheckerField.check;
import static org.epam.util.sub_controller.SubControllerMenu.existingUsernames;

@Service
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepo traineeRepo;
    private final ObjectMapper objectMapper;
    private static final Log log = LogFactory.getLog(TraineeServiceImpl.class);

    @Autowired
    public TraineeServiceImpl(TraineeRepo traineeRepo, ObjectMapper objectMapper) {
        this.traineeRepo = traineeRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    public TraineeDto save(Trainee t) {
        log.info("Saving Trainee...");
        existingUsernames.add(t.getUsername());
        return objectMapper.convertValue(traineeRepo.save(t), TraineeDto.class);
    }

    @Override
    public TraineeDto update(Integer id, Trainee t) {
        log.info("update Trainee...");
        Trainee trainee = traineeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        if (check(t.getAddress())) trainee.setAddress(t.getAddress());
        if (check(t.getPassword())) trainee.setPassword(t.getPassword());
        if (check(t.getUsername())) trainee.setUsername(t.getUsername());
        if (check(t.getFirstName())) trainee.setFirstName(t.getFirstName());
        if (check(t.getLastName())) trainee.setLastName(t.getLastName());
        if (check(String.valueOf(t.getDateOfBirth()))) trainee.setDateOfBirth(t.getDateOfBirth());
        return objectMapper.convertValue(traineeRepo.update(trainee), TraineeDto.class);
    }

    @Override
    public void delete(Integer id) {
        log.info("delete Trainee...");
        existingUsernames.remove(findById(id).username());
        traineeRepo.delete(id);
    }

    @Override
    public List<TraineeDto> findAll() {
        return traineeRepo.findAll()
                .stream()
                .map(trainee ->
                        objectMapper.convertValue(trainee, TraineeDto.class))
                .toList();
    }

    @Override
    public TraineeDto findById(Integer id) {
        return objectMapper.convertValue(traineeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainee not found")), TraineeDto.class);
    }
}
