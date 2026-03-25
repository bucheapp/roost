package io.github.bucheapp.roost.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.Role;

public interface RoleRepository extends JpaRepository<Role,Long> {
	Optional<Role> findByName(String name);
}
