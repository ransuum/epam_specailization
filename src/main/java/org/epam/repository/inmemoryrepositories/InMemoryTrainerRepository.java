package org.epam.repository.inmemoryrepositories;

import org.epam.models.entity.Trainer;
import org.epam.repository.TrainerRepo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryTrainerRepository implements TrainerRepo {
    private final Map<Integer, Object> trainers;

    public InMemoryTrainerRepository(@Qualifier("storageMap") Map<String, Map<Integer, Object>> storageMap) {
        this.trainers = storageMap.get("trainers");
    }

    @Override
    public Trainer save(Trainer trainer) {
        trainers.put(trainer.getId(), trainer);
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        return (Trainer) trainers.replace(trainer.getId(), trainer);
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
                .map(Trainer.class::cast)
                .sorted(Comparator.comparing(Trainer::getId))
                .toList();
    }

    @Override
    public Optional<Trainer> findById(Integer integer) {
        return Optional.ofNullable((Trainer) trainers.get(integer));
    }
}
