package io.github.bucheapp.roost.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.github.bucheapp.roost.dto.request.ChatRequest;
import io.github.bucheapp.roost.dto.response.TextChatResponse;

@Entity
@DiscriminatorValue("TEXT")
public class TextChat extends Chat {
	@Column(length = 500)
	@NotBlank
	@Size(min = 1,max = 500)
	private String content;
	
	@Column
	@Embedded
	private MediaContent mediaContent;
	
	@Column
	private boolean edited;
	
	public TextChat() {}
	
	public TextChat(
			long publicId,
			LocalDateTime createdAt,
			User creator,
			Room room,
			ChatRequest req) {
		super(
			publicId,
			createdAt,
			creator,
			room
			);
		this.content = req.getContent();
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

	@Override
	public TextChatResponse toResponse() {
		TextChatResponse res = new TextChatResponse(this);
		return res;
	}
}
