package io.github.bucheapp.roost.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.ChatRequest;
import io.github.bucheapp.roost.dto.request.UpdateChatRequest;
import io.github.bucheapp.roost.dto.response.ChatResponse;
import io.github.bucheapp.roost.dto.response.ChatSWResponse;
import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.models.SWType;
import io.github.bucheapp.roost.services.ChatService;

@RestController
@RequestMapping("api/rooms/{publicId}/chats")
public class ChatController {
	@Autowired
	private ChatService chatService;
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@GetMapping
	public ResponseEntity<ChatResponse> getChat(
			@PathVariable long publicId
			) {
		Chat chat = chatService.getChat(publicId);
		
		ChatResponse chatResponse = new ChatResponse(chat);
		
		return ResponseEntity.ok(chatResponse);
	}
	
	@PreAuthorize("hasAuthority('CREATE_CHAT')")
	@PostMapping
	public ResponseEntity<ChatResponse> createChat(
			@PathVariable long publicId,
			@RequestBody ChatRequest req
			) {
		Chat chat = chatService.createChat(publicId,req);
		
		ChatResponse chatResponse = new ChatResponse(chat);
		
		ChatSWResponse chatSWResponse = new ChatSWResponse(chat,SWType.NEW);
		template.convertAndSend("/topic/room/" + chatResponse.getRoomId() + "/chat", chatSWResponse);
		
		return ResponseEntity.ok(chatResponse);
	}
	
	@PreAuthorize("hasAuthority('UPDATE_CHAT')")
	@PatchMapping
	public ResponseEntity<ChatResponse> updateChat(
			@PathVariable long publicId,
			@RequestBody UpdateChatRequest req
			) {
		Chat chat = chatService.updateChat(publicId, req);
		
		ChatResponse chatResponse = new ChatResponse(chat);
		
		ChatSWResponse chatSWResponse = new ChatSWResponse(chat,SWType.UPDATE);
		template.convertAndSend("/topic/chat/" + publicId, chatSWResponse);
		
		return ResponseEntity.ok(chatResponse);
	}
	
	@PreAuthorize("hasAuthority('DELETE_CHAT')")
	@DeleteMapping
	public ResponseEntity<Void> deleteChat(
			@PathVariable long publicId
			) {
		chatService.deleteChat(publicId);
		
		Map<String, Object> payload = Map.of(
				"publicId", publicId,
				"swType", SWType.DELETE
			);
		template.convertAndSend("/topic/chat/" + publicId, (Object) payload);
		
		return ResponseEntity.noContent().build();
	}
}
