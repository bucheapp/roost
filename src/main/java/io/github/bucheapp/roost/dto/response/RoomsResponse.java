package io.github.bucheapp.roost.dto.response;

import java.util.List;

import io.github.bucheapp.roost.models.Room;

public class RoomsResponse {
	List<RoomResponse> rooms;
	
	public RoomsResponse(List<Room> rooms) {
		this.rooms = rooms.stream()
					.map(RoomResponse::new)
					.toList();
	}

	public List<RoomResponse> getRooms() {
		return rooms;
	}

	public void setRooms(List<RoomResponse> rooms) {
		this.rooms = rooms;
	}
}
