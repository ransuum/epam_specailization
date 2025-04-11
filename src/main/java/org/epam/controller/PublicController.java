package org.epam.controller;

import lombok.RequiredArgsConstructor;
import org.epam.models.dto.TrainingTypeDto;
import org.epam.service.TrainingTypeService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {
    private final TrainingTypeService trainingTypeService;

    @GetMapping("/training-type/all")
    public ResponseEntity<PagedModel<EntityModel<TrainingTypeDto>>> findAll(
            @ParameterObject @PageableDefault(sort = "trainingTypeName,asc") Pageable pageable,
            PagedResourcesAssembler<TrainingTypeDto> assembler) {
        final var trainingTypePages = trainingTypeService.findAll(pageable);
        return ResponseEntity.ok(assembler.toModel(trainingTypePages));
    }
}
