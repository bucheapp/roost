package io.github.bucheapp.roost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
	
}
