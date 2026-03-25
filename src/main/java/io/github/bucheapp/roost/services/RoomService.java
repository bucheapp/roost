package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.request.RoomRequest;
import io.github.bucheapp.roost.dto.request.RoomUpdateRequest;
import io.github.bucheapp.roost.models.Room;

public interface RoomService {
	Room getRoomByPublicId(long publicId);
	Room createRoom(long id,RoomRequest req);
	Room updateRoomByPublicId(long publicId,RoomUpdateRequest req);
}
