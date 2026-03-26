package io.github.bucheapp.roost.dto.request;

import io.github.bucheapp.roost.models.ChatType;

public class UpdateChatRequest {
	private String content;
	private ChatType type;
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
}
