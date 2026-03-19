package io.github.bucheapp.roost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.RefreshToken;
import reactor.core.publisher.Mono;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
	Mono<Void> deleteByToken(String token);
}
