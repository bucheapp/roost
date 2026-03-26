package io.github.bucheapp.roost.services;

import java.time.LocalDateTime;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.bucheapp.roost.dto.request.RoomRequest;
import io.github.bucheapp.roost.dto.request.RoomUpdateRequest;
import io.github.bucheapp.roost.models.Room;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.RoomRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.util.Snowflake;

@Service
public class RoomServiceImpl implements RoomService {
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WorkerIdProvider workerIdProvider;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	public Room getRoomByPublicId(long publicId) {
		return roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("ルームが見つかりません"));
	}

	@Override
	@Transactional
	public Room createRoom(long id,long publicId, RoomRequest req) {
		String name = req.getName();
		
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		LocalDateTime now = LocalDateTime.now();
		
		Room room = new Room();
		room.setName(name);
		room.setCreatedAt(now);
		room.setPublicId(snowflake.nextId());
		room.setCreator(user);
		
		return roomRepository.save(room);
	}

	@Override
	@Transactional
	public Room updateRoomByPublicId(long publicId, RoomUpdateRequest req) {
		String name = req.getName();
		Room room = roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("ルームが見つかりません"));
		
		LocalDateTime now = LocalDateTime.now();
		
		room.setName(name);
		room.setUpdatedAt(now);
		
		return roomRepository.save(room);
	}

	@Override
	@Transactional
	public void deleteRoomByPublicId(long publicId) {
		Room room = roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("ルームが見つかりません"));
		
		roomRepository.delete(room);
	}
}
