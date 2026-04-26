package io.github.bucheapp.roost.dto.response;

import java.util.List;

import io.github.bucheapp.roost.models.Chat;

public class ChatsResponse {
	List<ChatResponse> chats;
	
	public ChatsResponse(
			List<Chat> chats
			) {
		for(Chat chat : chats) {
			this.chats.add(
					chat.toResponse()
					);
		}
	}
}
