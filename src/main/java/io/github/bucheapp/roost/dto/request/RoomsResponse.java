package io.github.bucheapp.roost.dto.request;

import java.util.List;

import io.github.bucheapp.roost.dto.response.RoomResponse;
import io.github.bucheapp.roost.models.Room;

public class RoomsResponse {
	List<RoomResponse> rooms;
	
	public RoomsResponse(List<Room> rooms) {
		this.rooms = rooms.stream()
					.map(RoomResponse::new)
					.toList();
	}
}
