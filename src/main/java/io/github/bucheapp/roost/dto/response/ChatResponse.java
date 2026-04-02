package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.models.ChatType;

public abstract class ChatResponse {
	private ChatType type;
	private long publicId;
	private long roomId;
	private long creatorId;
	
	public ChatResponse(Chat chat) {
		this.type = ChatType.BASE;
		this.publicId = chat.getPublicId();
		this.roomId = chat.getRoom().getPublicId();
		this.creatorId = chat.getCreator().getPublicId();
	}
	
	public ChatType getType() {
		return type;
	}
	public void setType(ChatType type) {
		this.type = type;
	}

	public long getPublicId() {
		return publicId;
	}

	public void setPublicId(long publicId) {
		this.publicId = publicId;
	}

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}
}
