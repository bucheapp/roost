package io.github.bucheapp.roost.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.models.Room;

public interface ChatRepository extends JpaRepository<Chat,Long> {
	Optional<Chat> findByPublicId(long publicId);
	Page<Chat> findByRoom(Room room, Pageable pageable);
}
