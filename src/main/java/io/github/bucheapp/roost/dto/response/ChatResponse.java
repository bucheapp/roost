package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.models.ChatType;

public class ChatResponse {
	private String content;
	private ChatType type;
	long creatorId;
	
	public ChatResponse(Chat chat) {
		this.content = chat.getContent();
		this.type = chat.getType();
		this.creatorId = chat.getCreator().getPublicId();
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public ChatType getType() {
		return type;
	}
	public void setType(ChatType type) {
		this.type = type;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}
}
