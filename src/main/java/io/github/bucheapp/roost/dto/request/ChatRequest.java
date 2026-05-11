package io.github.bucheapp.roost.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import io.github.bucheapp.roost.models.ChatType;
import io.github.bucheapp.roost.models.MediaType;

public class ChatRequest {
	private ChatType type;
	
	// Text用
	@NotBlank(message="{chat.content.notBlank}")
	@Size(min = 1,max = 500,message="{chat.content.size}")
	private String content;
	private MultipartFile file;
	private MediaType mediaType;
	
	public ChatType getType() {
		return type;
	}
	public void setType(ChatType type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public MediaType getMediaType() {
		return mediaType;
	}
	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
}
