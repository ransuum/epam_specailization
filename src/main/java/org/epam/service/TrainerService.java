package org.epam.service;


import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TrainerDto;
import org.epam.models.dto.create.TrainerCreateDto;
import org.epam.models.dto.update.TrainerUpdateDto;
import org.epam.models.entity.Trainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrainerService {
    Trainer save(TrainerCreateDto trainerCreateDto) throws NotFoundException;

    TrainerDto update(TrainerUpdateDto request) throws NotFoundException;

    void delete(String id) throws NotFoundException;

    Page<TrainerDto> findAll(Pageable pageable) throws NotFoundException;

    TrainerDto findById(String id) throws NotFoundException;

    TrainerDto profile() throws NotFoundException;

    TrainerDto changePassword(String oldPassword, String newPassword) throws NotFoundException, CredentialException;

    TrainerDto findByUsername(String username) throws NotFoundException;

    TrainerDto changeStatus(String username) throws NotFoundException;

    List<TrainerDto> getUnassignedTrainers(String username) throws NotFoundException;
}
