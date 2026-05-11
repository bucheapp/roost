package io.github.bucheapp.roost.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.Room;

public interface RoomRepository extends JpaRepository<Room,Long> {
	Optional<Room> findByPublicId(long publicId);
	List<Room> findByCommunityId(long communityId);
}
