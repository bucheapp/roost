package io.github.bucheapp.roost.controllers;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.RoomRequest;
import io.github.bucheapp.roost.dto.request.UpdateRoomRequest;
import io.github.bucheapp.roost.dto.response.ChatsResponse;
import io.github.bucheapp.roost.dto.response.RoomResponse;
import io.github.bucheapp.roost.dto.response.sw.RoomSWResponse;
import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.models.Room;
import io.github.bucheapp.roost.models.SWType;
import io.github.bucheapp.roost.services.RoomService;

@RestController
public class RoomController {
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@GetMapping("api/rooms/{publicId}/chats")
	public ResponseEntity<ChatsResponse> getChats(
			@PathVariable long publicId,
			Pageable pageable
			) {
		List<Chat> chats = roomService.getChats(publicId,pageable);
		ChatsResponse chatsResponse = new ChatsResponse(chats);
		
		return ResponseEntity.ok(chatsResponse);
	}
	
	@GetMapping("api/rooms/{publicId}")
	public ResponseEntity<RoomResponse> getRoom(
			@PathVariable long publicId
			) {
		Room room = roomService.getRoom(publicId);
		
		RoomResponse roomResponse = new RoomResponse(room);
		
		return ResponseEntity.ok(roomResponse);
	}
	
	@PreAuthorize("hasAuthority('CREATE_ROOM') or @communitySecurity.isHost(#publicId)")
	@PostMapping("api/communities/{publicId}/rooms")
	public ResponseEntity<RoomResponse> createRoom(
			@PathVariable long publicId,
			@Valid @RequestBody RoomRequest req
			) {
		
		Room room = roomService.createRoom(publicId, req);
		
		RoomResponse roomResponse = new RoomResponse(room);
		
		RoomSWResponse roomSWResponse = new RoomSWResponse(room,SWType.NEW);
		template.convertAndSend("/topic/community/" + roomResponse.getCommunityId() + "/room", roomSWResponse);
		
		return ResponseEntity.ok(roomResponse);
	}
	
	@PreAuthorize("hasAuthority('UPDATE_ROOM') or @communitySecurity.isHost(#publicId)")
	@PatchMapping("api/rooms/{publicId}")
	public ResponseEntity<RoomResponse> updateRoom(
			@PathVariable long publicId,
			@Valid @RequestBody UpdateRoomRequest req
			) {
		Room room = roomService.updateRoom(publicId, req);
		
		RoomResponse roomResponse = new RoomResponse(room);
		
		RoomSWResponse roomSWResponse = new RoomSWResponse(room,SWType.NEW);
		template.convertAndSend("/topic/room/" + publicId, roomSWResponse);
		
		return ResponseEntity.ok(roomResponse);
	}
	
	@PreAuthorize("hasAuthority('DELETE_ROOM') or @communitySecurity.isHost(#publicId)")
	@DeleteMapping("api/rooms/{publicId}")
	public ResponseEntity<Void> deleteRoom(
			@PathVariable long publicId
			) {
		roomService.deleteRoom(publicId);
		
		Map<String, Object> payload = Map.of(
				"publicId", publicId,
				"swType", SWType.DELETE
			);
		template.convertAndSend("/topic/room/" + publicId, (Object) payload);
		
		return ResponseEntity.noContent().build();
	}
}
