package org.epam.repository.inmemoryrepositories;

import org.epam.models.entity.Trainee;
import org.epam.repository.TraineeRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryTraineeRepository implements TraineeRepository {
    private final Map<Integer, Object> trainees;

    public InMemoryTraineeRepository(@Qualifier("storageMap") Map<String, Map<Integer, Object>> storageMap) {
        this.trainees = storageMap.get("trainees");
    }

    @Override
    public Trainee save(Trainee trainee) {
        trainees.put(trainee.getId(), trainee);
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        return (Trainee) trainees.replace(trainee.getId(), trainee);
    }

    @Override
    public void delete(Integer id) {
        trainees.remove(findById(id).orElseThrow(()
                -> new RuntimeException("Trainee not found")).getId()
        );
    }

    @Override
    public List<Trainee> findAll() {
        return trainees.values()
                .stream()
                .map(Trainee.class::cast)
                .sorted(Comparator.comparing(Trainee::getId))
                .toList();
    }

    @Override
    public Optional<Trainee> findById(Integer id) {
        return Optional.ofNullable((Trainee) trainees.get(id));
    }
}
