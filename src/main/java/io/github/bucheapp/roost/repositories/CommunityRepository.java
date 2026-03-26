package io.github.bucheapp.roost.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.Community;

public interface CommunityRepository extends JpaRepository<Community,Long> {
	Optional<Community> findByPublicId(long publicId);
	List<Community> findByName(String name);
}
