package io.github.bucheapp.roost.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.models.User;

public interface ProfileRepository extends JpaRepository<Profile,Long> {
	Optional<Profile> findByUserId(long userId);
	Optional<Profile> findByUser(User user);
}
