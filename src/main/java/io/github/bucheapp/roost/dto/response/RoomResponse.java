package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.Room;

public class RoomResponse {
	private String name;
	
	public RoomResponse(Room room) {
		this.name = room.getName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
