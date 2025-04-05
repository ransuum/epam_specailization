package org.epam.repository;

import org.epam.models.entity.RefreshToken;
import org.epam.models.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String refreshToken);

    Optional<RefreshToken> findByUser(Users user);

    List<RefreshToken> findAllByExpiresAtBeforeOrRevokedIsTrue(Instant now);
}
