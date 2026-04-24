package io.github.bucheapp.roost.dto.response;

import java.util.UUID;

import io.github.bucheapp.roost.models.ChatType;
import io.github.bucheapp.roost.models.TextChat;

public class TextChatResponse extends ChatResponse {
	private String content;
	private UUID mediaContentUUID;
	
	public TextChatResponse(TextChat textChat) {
		super(textChat);
		setType(ChatType.TEXT);
		this.content = textChat.getContent();
		this.mediaContentUUID = textChat.getMediaContentUUID();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UUID getMediaContentUUID() {
		return mediaContentUUID;
	}

	public void setMediaContentUUID(UUID mediaContentUUID) {
		this.mediaContentUUID = mediaContentUUID;
	}
}
