package org.epam.service;

import org.epam.models.entity.Trainee;

import java.util.List;

public interface TraineeService {
    Trainee save(Trainee t);

    Trainee update(Trainee t);

    void delete(Integer id);

    List<Trainee> findAll();

    Trainee findById(Integer id);
}
