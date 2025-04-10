package org.epam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.epam.models.dto.TraineeDto;
import org.epam.models.dto.update.TraineeRequestDto;
import org.epam.service.TraineeService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainee")
@RequiredArgsConstructor
@Tag(name = "Trainee Management", description = "APIs for managing trainee operations")
public class TraineeController {
    private final TraineeService traineeService;

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAuthority('SCOPE_SEARCH_TRAINEES')")
    public ResponseEntity<TraineeDto> findById(@PathVariable String id) {
        return new ResponseEntity<>(traineeService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('SCOPE_VIEW_TRAINEE_PROFILE')")
    public ResponseEntity<TraineeDto> profile() {
        return new ResponseEntity<>(traineeService.profile(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_FULL_ACCESS')")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        traineeService.delete(id);
        return ResponseEntity.ok("Deleted successfully!");
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('SCOPE_VIEW_TRAINEE_PROFILE')")
    public ResponseEntity<TraineeDto> updateTrainee(@RequestBody @Valid TraineeRequestDto traineeRequestDto) {
        return ResponseEntity.ok(traineeService.update(traineeRequestDto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_SEARCH_TRAINEES')")
    public ResponseEntity<PagedModel<EntityModel<TraineeDto>>> findAll(
            @ParameterObject @PageableDefault(sort = "firstName,asc") Pageable pageable,
            PagedResourcesAssembler<TraineeDto> assembler) {
        final var traineePages = traineeService.findAll(pageable);
        return new ResponseEntity<>(assembler.toModel(traineePages), HttpStatus.OK);
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasAuthority('SCOPE_VIEW_TRAINEE_PROFILE')")
    public ResponseEntity<String> changePassword(@RequestParam String oldPassword,
                                                 @RequestParam String newPassword) {
        traineeService.changePassword(oldPassword, newPassword);
        return ResponseEntity.ok("Password changed");
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAuthority('SCOPE_SEARCH_TRAINEES')")
    public ResponseEntity<TraineeDto> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(traineeService.findByUsername(username));
    }

    @DeleteMapping("/username/{username}")
    @PreAuthorize("hasAuthority('SCOPE_FULL_ACCESS')")
    public ResponseEntity<String> deleteTraineeByUsername(@PathVariable String username) {
        return ResponseEntity.ok(traineeService.deleteByUsername(username));
    }

    @PatchMapping("/change-status")
    @PreAuthorize("hasAuthority('SCOPE_VIEW_TRAINEE_PROFILE')")
    public ResponseEntity<String> changeStatus() {
        traineeService.changeStatus();
        return ResponseEntity.ok("Changed status successfully");
    }
}
