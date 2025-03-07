package org.epam.repository.configuration;

import org.epam.models.entity.Trainer;
import org.epam.repository.TrainerRepo;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TrainerRepository implements TrainerRepo {
    private final Map<Integer, Trainer> trainers = new HashMap<>();

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
