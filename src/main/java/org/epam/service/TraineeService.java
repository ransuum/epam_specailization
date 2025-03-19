package org.epam.service;


import org.epam.models.dto.TraineeDto;
import org.epam.models.request.traineeRequest.TraineeRequestCreate;
import org.epam.models.request.traineeRequest.TraineeRequestUpdate;

import java.util.List;

public interface TraineeService {
    TraineeDto save(TraineeRequestCreate request);

    TraineeDto update(String id, TraineeRequestUpdate request);

    void delete(String id);

    List<TraineeDto> findAll();

    TraineeDto findById(String id);

    TraineeDto changePassword(String id, String oldPassword, String newPassword);

    TraineeDto findByUsername(String username);

    String deleteByUsername(String username);
    TraineeDto activateAction(String username);

    TraineeDto deactivateAction(String username);
}
