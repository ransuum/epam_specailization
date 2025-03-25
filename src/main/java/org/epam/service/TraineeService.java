package org.epam.service;


import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TraineeDto;
import org.epam.models.request.create.TraineeRequestCreate;

import java.util.List;

public interface TraineeService {
    TraineeDto save(TraineeRequestCreate request) throws NotFoundException;

    TraineeDto update(String id, org.epam.models.request.update.TraineeRequestUpdate request) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TraineeDto> findAll();

    TraineeDto findById(String id) throws NotFoundException;

    TraineeDto changePassword(String id, String oldPassword, String newPassword) throws NotFoundException, CredentialException;

    TraineeDto findByUsername(String username) throws NotFoundException;

    String deleteByUsername(String username) throws NotFoundException;

    TraineeDto changeStatus(String username) throws NotFoundException;
}
