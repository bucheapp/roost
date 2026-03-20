package io.github.bucheapp.roost.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
	Optional<Void> deleteByToken(String token);
	Optional<RefreshToken> findByToken(String token);
}
