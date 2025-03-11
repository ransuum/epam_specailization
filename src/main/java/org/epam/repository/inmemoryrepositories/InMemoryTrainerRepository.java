package org.epam.repository.inmemoryrepositories;

import org.epam.models.entity.Trainee;
import org.epam.models.entity.Trainer;
import org.epam.repository.TrainerRepo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryTrainerRepository implements TrainerRepo {
    private final Map<Integer, Trainer> trainers;

    public InMemoryTrainerRepository(@Qualifier("trainersStorage") Map<Integer, Trainer> storageMap) {
        this.trainers = storageMap;
    }

    @Override
    public Trainer save(Trainer trainer) {
        trainers.put(trainer.getId(), trainer);
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        return trainers.replace(trainer.getId(), trainer);
    }

    @Override
    public void delete(Integer integer) {
        trainers.remove(findById(integer).orElseThrow(()
                -> new RuntimeException("Trainee not found")).getId()
        );
    }

    @Override
    public List<Trainer> findAll() {
        return trainers.values()
                .stream()
                .sorted(Comparator.comparing(Trainer::getId))
                .toList();
    }

    @Override
    public Optional<Trainer> findById(Integer integer) {
        return Optional.ofNullable(trainers.get(integer));
    }
}
