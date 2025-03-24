package org.epam.service;


import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.TrainerDto;
import org.epam.models.request.createrequest.TrainerRequestCreate;
import org.epam.models.request.updaterequest.TrainerRequestUpdate;

import java.util.List;

public interface TrainerService {
    TrainerDto save(TrainerRequestCreate request) throws NotFoundException;

    TrainerDto update(String id, TrainerRequestUpdate request) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    List<TrainerDto> findAll() throws NotFoundException;

    TrainerDto findById(String id) throws NotFoundException;

    TrainerDto changePassword(String id, String oldPassword, String newPassword) throws NotFoundException, CredentialException;

    TrainerDto findByUsername(String username) throws NotFoundException;

    TrainerDto activateAction(String username) throws NotFoundException;

    TrainerDto deactivateAction(String username) throws NotFoundException;

    List<TrainerDto> getUnassignedTrainersForTrainee(String username) throws NotFoundException;
}
