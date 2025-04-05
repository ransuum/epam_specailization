package org.epam.service;

import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TraineeDto;
import org.epam.models.dto.create.TraineeCreateDto;
import org.epam.models.dto.update.TraineeRequestDto;
import org.epam.models.entity.Trainee;

import java.util.List;

public interface TraineeService {
    Trainee save(TraineeCreateDto request) throws NotFoundException;

    TraineeDto update(TraineeRequestDto request) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TraineeDto> findAll();

    TraineeDto findById(String id) throws NotFoundException;

    TraineeDto profile() throws NotFoundException;

    TraineeDto changePassword(String oldPassword, String newPassword) throws NotFoundException, CredentialException;

    TraineeDto findByUsername(String username) throws NotFoundException;

    String deleteByUsername(String username) throws NotFoundException;

    TraineeDto changeStatus() throws NotFoundException;
}
