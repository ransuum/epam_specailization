package org.epam.service;


import org.epam.models.dto.TrainerDto;
import org.epam.models.request.trainerrequest.TrainerRequestCreate;
import org.epam.models.request.trainerrequest.TrainerRequestUpdate;

import java.util.List;

public interface TrainerService {
    TrainerDto save(TrainerRequestCreate request);

    TrainerDto update(String id, TrainerRequestUpdate request);

    void delete(String id);

    List<TrainerDto> findAll();

    TrainerDto findById(String id);

    TrainerDto changePassword(String id, String oldPassword, String newPassword);

    TrainerDto findByUsername(String username);

    TrainerDto activateAction(String username);

    TrainerDto deactivateAction(String username);
}
