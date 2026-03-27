package io.github.bucheapp.roost.services;

import java.time.LocalDateTime;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.RoomRequest;
import io.github.bucheapp.roost.dto.request.UpdateRoomRequest;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.Room;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.CommunityRepository;
import io.github.bucheapp.roost.repositories.RoomRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.AuthContext;
import io.github.bucheapp.roost.util.Snowflake;

@Service
public class RoomServiceImpl implements RoomService {
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CommunityRepository communityRepository;
	
	@Autowired
	private WorkerIdProvider workerIdProvider;
	
	@Autowired
	private AuthContext authContext;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	public Room getRoom(long publicId) {
		return roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
	}

	@Override
	@Transactional
	public Room createRoom(long publicId, RoomRequest req) {
		long userId = authContext.getCurrentUserId();
		String name = req.getName();
		
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		LocalDateTime now = LocalDateTime.now();
		
		Room room = new Room();
		room.setName(name);
		room.setCreatedAt(now);
		room.setPublicId(snowflake.nextId());
		room.setCreator(user);
		room.setCommunity(community);
		
		return roomRepository.save(room);
	}

	@Override
	@Transactional
	public Room updateRoom(long publicId, UpdateRoomRequest req) {
		String name = req.getName();
		Room room = roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
		
		LocalDateTime now = LocalDateTime.now();
		
		room.setName(name);
		room.setUpdatedAt(now);
		
		return roomRepository.save(room);
	}

	@Override
	@Transactional
	public void deleteRoom(long publicId) {
		Room room = roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
		
		roomRepository.delete(room);
	}
}
