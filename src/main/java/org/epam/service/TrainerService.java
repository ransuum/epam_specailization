package org.epam.service;


import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainerDto;
import org.epam.models.request.create.TrainerRequestCreate;

import java.util.List;

public interface TrainerService {
    TrainerDto save(TrainerRequestCreate request) throws NotFoundException;

    TrainerDto update(String id, org.epam.models.request.update.TrainerRequestUpdate request) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TrainerDto> findAll() throws NotFoundException;

    TrainerDto findById(String id) throws NotFoundException;

    TrainerDto changePassword(String id, String oldPassword, String newPassword) throws NotFoundException, CredentialException;

    TrainerDto findByUsername(String username) throws NotFoundException;

    TrainerDto changeStatus(String username) throws NotFoundException;

    List<TrainerDto> getUnassignedTrainersForTrainee(String username) throws NotFoundException;
}
