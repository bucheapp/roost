package io.github.bucheapp.roost.services;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.RoomRequest;
import io.github.bucheapp.roost.dto.request.UpdateRoomRequest;
import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.CommunityState;
import io.github.bucheapp.roost.models.Room;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.ChatRepository;
import io.github.bucheapp.roost.repositories.CommunityRepository;
import io.github.bucheapp.roost.repositories.RoomRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.AuthContext;
import io.github.bucheapp.roost.util.MessageUtil;
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
	private ChatRepository chatRepository;
	
	@Autowired
	private WorkerIdProvider workerIdProvider;
	
	@Autowired
	private AuthContext authContext;
	
	@Autowired
	private MessageUtil messageUtil;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	public Room getRoom(long publicId) {
		return roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("room.notfound")));
	}

	@Override
	@Transactional
	public Room createRoom(long publicId, RoomRequest req) {
		long userId = authContext.getCurrentUserId();
		String name = req.getName();
		
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));
		
		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("community.notactive"));
		}
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		
		Room room = new Room(
				snowflake.nextId(),
				name,
				LocalDateTime.now(),
				user,
				community
				);
		
		return roomRepository.save(room);
	}

	@Override
	@Transactional
	public Room updateRoom(long publicId, UpdateRoomRequest req) {
		String name = req.getName();
		Room room = roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("room.notfound")));
		
		LocalDateTime now = LocalDateTime.now();
		
		room.setName(name);
		room.setUpdatedAt(now);
		
		Community community = room.getCommunity();
		
		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("community.notactive"));
		}
		
		return roomRepository.save(room);
	}

	@Override
	@Transactional
	public void deleteRoom(long publicId) {
		Room room = roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("room.notfound")));
		
		Community community = room.getCommunity();
		
		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("community.notactive"));
		}
		
		roomRepository.delete(room);
	}
	
	@Override
	public List<Chat> getChats(
			long publicId,
			Pageable pageable
			) {
		Room room = roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("room.notfound")));
		
		List<Chat> chats = chatRepository.findByRoom(room, pageable).toList();
		
		return chats;
	}
}
