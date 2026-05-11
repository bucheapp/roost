package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.ChatType;
import io.github.bucheapp.roost.models.MediaContent;
import io.github.bucheapp.roost.models.TextChat;

public class TextChatResponse extends ChatResponse {
	private String content;
	private MediaContent mediaContent;
	private boolean edited;
	
	public TextChatResponse(TextChat textChat) {
		super(textChat);
		setType(ChatType.TEXT);
		this.content = textChat.getContent();
		this.mediaContent = textChat.getMediaContent();
		this.edited = textChat.isEdited();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public MediaContent getMediaContent() {
		return mediaContent;
	}

	public void setMediaContent(MediaContent mediaContent) {
		this.mediaContent = mediaContent;
	}

	public boolean isEdited() {
		return edited;
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
	}
}
