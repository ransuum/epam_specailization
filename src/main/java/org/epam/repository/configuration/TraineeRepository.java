package org.epam.repository.configuration;

import org.epam.models.entity.Trainee;
import org.epam.repository.TraineeRepo;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TraineeRepository implements TraineeRepo {
    private final Map<Integer, Trainee> trainees = new HashMap<>();

    @Override
    public Trainee save(Trainee trainee) {
        trainees.put(trainee.getId(), trainee);
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        return trainees.replace(trainee.getId(), trainee);
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
                .sorted(Comparator.comparing(Trainee::getId))
                .toList();
    }

    @Override
    public Optional<Trainee> findById(Integer id) {
        return Optional.ofNullable(trainees.get(id));
    }
}
