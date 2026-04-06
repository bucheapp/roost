package io.github.bucheapp.roost.models;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
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
	private String mediaContentUrl;
	
	public TextChat() {}
	
	public TextChat(ChatRequest req) {
		this.content = req.getContent();
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
	
	@Override
	public TextChatResponse toResponse() {
		TextChatResponse res = new TextChatResponse(this);
		return res;
	}
}
