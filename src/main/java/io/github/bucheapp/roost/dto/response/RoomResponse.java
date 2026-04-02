package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.Room;

public class RoomResponse {
	private String name;
	private long communityId;
	private long publicId;
	
	public RoomResponse(Room room) {
		this.name = room.getName();
		this.communityId = room.getCommunity().getPublicId();
		this.publicId = room.getPublicId();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCommunityId() {
		return communityId;
	}

	public void setCommunityId(long communityId) {
		this.communityId = communityId;
	}

	public long getPublicId() {
		return publicId;
	}

	public void setPublicId(long publicId) {
		this.publicId = publicId;
	}
}
