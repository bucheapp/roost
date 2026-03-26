package io.github.bucheapp.roost.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.ChatRequest;
import io.github.bucheapp.roost.dto.request.ChatUpdateRequest;
import io.github.bucheapp.roost.dto.response.ChatResponse;
import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.security.CustomUserDetails;
import io.github.bucheapp.roost.services.ChatService;

@RestController
@RequestMapping("api/rooms/{publicId}/chats")
public class ChatController {
	@Autowired
	private ChatService chatService;
	
	@GetMapping
	public ResponseEntity<ChatResponse> getChat(
			@PathVariable long publicId
			) {
		Chat chat = chatService.getChatByPublicId(publicId);
		
		ChatResponse chatResponse = new ChatResponse(chat);
		
		return ResponseEntity.ok(chatResponse);
	}
	
	@PreAuthorize("hasAuthority('CREATE_CHAT')")
	@PostMapping
	public ResponseEntity<ChatResponse> createChat(
			@PathVariable long publicId,
			@RequestBody ChatRequest req,
			Authentication authentication
			) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long id = userDetails.getId();
		
		Chat chat = chatService.createChat(id,publicId,req);
		
		ChatResponse chatResponse = new ChatResponse(chat);
		
		return ResponseEntity.ok(chatResponse);
	}
	
	@PreAuthorize("hasAuthority('UPDATE_CHAT')")
	@PatchMapping
	public ResponseEntity<ChatResponse> updateChat(
			@PathVariable long publicId,
			@RequestBody ChatUpdateRequest req
			) {
		Chat chat = chatService.updateChatByPublicId(publicId, req);
		
		ChatResponse chatResponse = new ChatResponse(chat);
		
		return ResponseEntity.ok(chatResponse);
	}
	
	@PreAuthorize("hasAuthority('DELETE_CHAT')")
	@DeleteMapping
	public ResponseEntity<Void> deleteChat(
			@PathVariable long publicId
			) {
		chatService.deleteChatByPublicId(publicId);
		
		return ResponseEntity.noContent().build();
	}
}
