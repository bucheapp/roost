package io.github.bucheapp.roost.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.Profile;

public interface ProfileRepository extends JpaRepository<Profile,Long> {
	Optional<Profile> findByUserId(long userId);
}
