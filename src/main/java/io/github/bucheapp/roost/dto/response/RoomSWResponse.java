package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.Room;
import io.github.bucheapp.roost.models.SWType;

public class RoomSWResponse extends RoomResponse {
	private SWType swType;
	
	public RoomSWResponse(Room room,SWType swType) {
		super(room);
		this.swType = swType;
	}

	public SWType getSwType() {
		return swType;
	}

	public void setSwType(SWType swType) {
		this.swType = swType;
	}
}
