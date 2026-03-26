package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.request.ChatRequest;
import io.github.bucheapp.roost.dto.request.ChatUpdateRequest;
import io.github.bucheapp.roost.models.Chat;

public interface ChatService {
	Chat getChatByPublicId(long publicId);
	Chat createChat(long id,long publicId,ChatRequest req);
	Chat updateChatByPublicId(long publicId,ChatUpdateRequest req);
	void deleteChatByPublicId(long publicId);
}
