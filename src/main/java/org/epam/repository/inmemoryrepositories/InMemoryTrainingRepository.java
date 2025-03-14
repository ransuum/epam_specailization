package org.epam.repository.inmemoryrepositories;

import org.epam.models.entity.Training;
import org.epam.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryTrainingRepository implements TrainingRepository {
    private final Map<Integer, Object> trainings;

    public InMemoryTrainingRepository(@Qualifier("storageMap") Map<String, Map<Integer, Object>> storageMap) {
        this.trainings = storageMap.get("trainings");
    }

    @Override
    public Training save(Training training) {
        trainings.put(training.getId(), training);
        return training;
    }

    @Override
    public Training update(Training training) {
        return (Training) trainings.replace(training.getId(), training);
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
                .map(Training.class::cast)
                .sorted(Comparator.comparing(Training::getId))
                .toList();
    }

    @Override
    public Optional<Training> findById(Integer integer) {
        return Optional.ofNullable((Training) trainings.get(integer));
    }
}
