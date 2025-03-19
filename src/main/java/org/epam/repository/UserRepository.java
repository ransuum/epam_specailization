package org.epam.repository;


import org.epam.models.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<String, User> {
    User save(User user);

    Optional<User> findById(String id);

    void delete(String id);

    List<User> findAll();

    boolean existsByUsername(String username);
}
