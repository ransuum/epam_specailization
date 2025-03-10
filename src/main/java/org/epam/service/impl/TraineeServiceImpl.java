package org.epam.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epam.exception.EntityNotFoundException;
import org.epam.models.dto.TraineeDto;
import org.epam.models.entity.Trainee;
import org.epam.repository.TraineeRepo;
import org.epam.service.TraineeService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.epam.util.CheckerField.check;
import static org.epam.util.subcontroller.SubControllerMenu.existingUsernames;

@Service
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepo traineeRepo;
    private final ObjectMapper objectMapper;
    private static final Log log = LogFactory.getLog(TraineeServiceImpl.class);

    public TraineeServiceImpl(TraineeRepo traineeRepo, ObjectMapper objectMapper) {
        this.traineeRepo = traineeRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    public TraineeDto save(Trainee trainee) {
        log.info("Saving Trainee...");
        existingUsernames.add(trainee.getUsername());
        return objectMapper.convertValue(traineeRepo.save(trainee), TraineeDto.class);
    }

    @Override
    public TraineeDto update(Integer id, Trainee trainee) {
        log.info("update Trainee...");
        Trainee traineeById = traineeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));

        if (check(trainee.getAddress())) traineeById.setAddress(trainee.getAddress());
        if (check(trainee.getPassword())) traineeById.setPassword(trainee.getPassword());
        if (check(trainee.getUsername())) traineeById.setUsername(trainee.getUsername());
        if (check(trainee.getFirstName())) traineeById.setFirstName(trainee.getFirstName());
        if (check(trainee.getLastName())) traineeById.setLastName(trainee.getLastName());
        if (check(String.valueOf(trainee.getDateOfBirth()))) traineeById.setDateOfBirth(trainee.getDateOfBirth());
        return objectMapper.convertValue(traineeRepo.update(traineeById), TraineeDto.class);
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
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found")), TraineeDto.class);
    }
}
