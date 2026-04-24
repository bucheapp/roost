package io.github.bucheapp.roost.services;

import java.io.IOException;

import io.github.bucheapp.roost.dto.request.ChatRequest;
import io.github.bucheapp.roost.models.Chat;

public interface ChatService {
	Chat getChat(long publicId);
	Chat createChat(long publicId,ChatRequest req) throws IOException;
	Chat updateChat(long publicId,ChatRequest req) throws IOException;
	void deleteChat(long publicId);
}
