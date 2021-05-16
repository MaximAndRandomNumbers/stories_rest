package ru.kuznetsov.stories.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kuznetsov.stories.models.VerificationToken;

public interface VerificationTokenDao extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
