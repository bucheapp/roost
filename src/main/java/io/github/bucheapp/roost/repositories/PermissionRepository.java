package io.github.bucheapp.roost.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.Permission;

public interface PermissionRepository extends JpaRepository<Permission,Long> {
	Optional<Permission> findByName(String name);
}
