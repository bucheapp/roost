package io.github.bucheapp.roost.services;

import org.springframework.web.multipart.MultipartFile;

import io.github.bucheapp.roost.dto.request.ChatRequest;
import io.github.bucheapp.roost.models.Chat;

public interface ChatService {
	Chat getChat(long publicId);
	Chat createChat(long publicId,ChatRequest req);
	Chat updateChat(long publicId,ChatRequest req);
	void deleteChat(long publicId);
	void checkByte(MultipartFile file);
}
