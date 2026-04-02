package io.github.bucheapp.roost.dto.request;

import org.springframework.web.multipart.MultipartFile;

import io.github.bucheapp.roost.models.ChatType;

public class ChatRequest {
	private ChatType type;
	
	// Text用
	private String content;
	private MultipartFile file;
	
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
}
