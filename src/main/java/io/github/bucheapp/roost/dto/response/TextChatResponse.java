package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.ChatType;
import io.github.bucheapp.roost.models.TextChat;

public class TextChatResponse extends ChatResponse {
	private String content;
	private String mediaContentUrl;
	
	public TextChatResponse(TextChat textChat) {
		super(textChat);
		setType(ChatType.TEXT);
		this.content = textChat.getContent();
		this.mediaContentUrl = textChat.getMediaContentUrl();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMediaContentUrl() {
		return mediaContentUrl;
	}

	public void setMediaContentUrl(String mediaContentUrl) {
		this.mediaContentUrl = mediaContentUrl;
	}
}
