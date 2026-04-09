package com.TeamDeadlock.ExploreBangladesh.auth.repository;

import com.TeamDeadlock.ExploreBangladesh.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByEmailAndUsedFalse(String email);

    @Query("DELETE FROM PasswordResetToken WHERE expiryDate < :now OR used = true")
    void deleteExpiredOrUsedTokens(LocalDateTime now);
}
