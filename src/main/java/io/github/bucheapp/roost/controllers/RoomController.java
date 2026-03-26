package io.github.bucheapp.roost.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import io.github.bucheapp.roost.dto.response.RoomResponse;
import io.github.bucheapp.roost.models.Room;
import io.github.bucheapp.roost.services.RoomService;

@RestController
public class RoomController {
	@Autowired
	private RoomService roomService;
	
	@GetMapping("api/rooms/{publicId}")
	public ResponseEntity<RoomResponse> getRoom(
			@PathVariable long publicId
			) {
		Room room = roomService.getRoom(publicId);
		
		RoomResponse roomResponse = new RoomResponse(room);
		
		return ResponseEntity.ok(roomResponse);
	}
	
	@PreAuthorize("hasAuthority('CREATE_ROOM')")
	@PostMapping("api/communities/{publicId}/rooms")
	public ResponseEntity<RoomResponse> createRoom(
			@PathVariable long publicId,
			@RequestBody RoomRequest req
			) {
		
		Room room = roomService.createRoom(publicId, req);
		
		RoomResponse roomResponse = new RoomResponse(room);
		
		return ResponseEntity.ok(roomResponse);
	}
	
	@PreAuthorize("hasAuthority('UPDATE_ROOM')")
	@PatchMapping("api/rooms/{publicId}")
	public ResponseEntity<RoomResponse> updateRoom(
			@PathVariable long publicId,
			@RequestBody UpdateRoomRequest req
			) {
		Room room = roomService.updateRoom(publicId, req);
		
		RoomResponse roomResponse = new RoomResponse(room);
		
		return ResponseEntity.ok(roomResponse);
	}
	
	@PreAuthorize("hasAuthority('DELETE_ROOM')")
	@DeleteMapping("api/rooms/{publicId}")
	public ResponseEntity<Void> deleteRoom(
			@PathVariable long publicId
			) {
		roomService.deleteRoom(publicId);
		
		return ResponseEntity.noContent().build();
	}
}
