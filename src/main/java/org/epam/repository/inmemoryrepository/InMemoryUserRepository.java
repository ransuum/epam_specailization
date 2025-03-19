package org.epam.repository.inmemoryrepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.exception.NotFoundException;
import org.epam.models.entity.User;
import org.epam.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final EntityManager entityManager;

    public InMemoryUserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private static final Logger log = LogManager.getLogger(InMemoryUserRepository.class);

    @Override
    @Transactional
    public User save(User user) {
        try {
            if (user.getId() == null) entityManager.persist(user);
            else user = entityManager.merge(user);
            return user;
        } catch (Exception e) {
            log.error("Error in saving trainee: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    @Transactional
    public void delete(String id) {
        try {
            User user = findById(id).orElseThrow(()
                    -> new NotFoundException("Not found trainingView by id: " + id));
            if (entityManager.contains(user)) entityManager.remove(user);
            else entityManager.merge(user);
        } catch (NotFoundException e) {
            log.error("Not found user by id: {}", id);
        }
    }

    @Override
    public List<User> findAll() {
        return entityManager.createQuery("from User ", User.class).getResultList();
    }

    @Override
    public boolean existsByUsername(String username) {
        return entityManager.createQuery(
                        "SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username", Boolean.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    @Override
    @Transactional
    public User update(String id, User userRequest) {
        findById(id).ifPresent(traineeById -> userRequest.setId(id));

        return entityManager.merge(userRequest);
    }
}
