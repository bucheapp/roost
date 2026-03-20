package io.github.bucheapp.roost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.bucheapp.roost.models.User;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	boolean existsByEmail(String mail);
	boolean existsByName(String name);
	Mono<User> findByName(String name);
}
