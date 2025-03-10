package org.epam.service;

import org.epam.models.dto.TraineeDto;
import org.epam.models.entity.Trainee;

import java.util.List;

public interface TraineeService {
    TraineeDto save(Trainee t);

    TraineeDto update(Integer id, Trainee t);

    void delete(Integer id);

    List<TraineeDto> findAll();

    TraineeDto findById(Integer id);
}
