package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.request.RoomRequest;
import io.github.bucheapp.roost.dto.request.UpdateRoomRequest;
import io.github.bucheapp.roost.models.Room;

public interface RoomService {
	Room getRoom(long publicId);
	Room createRoom(long publicId,RoomRequest req);
	Room updateRoom(long publicId,UpdateRoomRequest req);
	void deleteRoom(long publicId);
}
