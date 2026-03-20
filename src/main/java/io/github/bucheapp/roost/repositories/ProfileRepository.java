package io.github.bucheapp.roost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.Profile;
import reactor.core.publisher.Mono;

public interface ProfileRepository extends JpaRepository<Profile,Long> {
	Mono<Profile> findByUserId(long userId);
}
