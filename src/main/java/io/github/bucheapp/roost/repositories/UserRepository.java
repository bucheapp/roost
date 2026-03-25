package io.github.bucheapp.roost.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.bucheapp.roost.models.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	boolean existsByEmail(String mail);
	boolean existsByName(String name);
	Optional<User> findByName(String name);
	Optional<User> findByPublicId(long publicId);
}
