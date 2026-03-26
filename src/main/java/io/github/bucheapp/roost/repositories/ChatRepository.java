package io.github.bucheapp.roost.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.Chat;

public interface ChatRepository extends JpaRepository<Chat,Long> {
	Optional<Chat> findByPublicId(long publicId);
}
