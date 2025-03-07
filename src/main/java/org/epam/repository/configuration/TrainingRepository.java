package org.epam.repository.configuration;

import org.epam.models.entity.Training;
import org.epam.repository.TrainingRepo;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TrainingRepository implements TrainingRepo {
    private final Map<Integer, Training> trainings = new HashMap<>();

    @Override
    public Training save(Training training) {
        trainings.put(training.getId(), training);
        return training;
    }

    @Override
    public Training update(Training training) {
        return trainings.replace(training.getId(), training);
    }

    @Override
    public void delete(Integer integer) {
        trainings.remove(findById(integer).orElseThrow(()
                -> new RuntimeException("Training not found")).getId());
    }

    @Override
    public List<Training> findAll() {
        return trainings.values()
                .stream()
                .sorted(Comparator.comparing(Training::getId))
                .toList();
    }

    @Override
    public Optional<Training> findById(Integer integer) {
        return Optional.ofNullable(trainings.get(integer));
    }
}
