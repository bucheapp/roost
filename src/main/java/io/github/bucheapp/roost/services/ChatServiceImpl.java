package io.github.bucheapp.roost.services;

import java.time.LocalDateTime;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.ChatRequest;
import io.github.bucheapp.roost.dto.request.UpdateChatRequest;
import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.models.ChatType;
import io.github.bucheapp.roost.models.Room;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.ChatRepository;
import io.github.bucheapp.roost.repositories.RoomRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.AuthContext;
import io.github.bucheapp.roost.util.Snowflake;

@Service
public class ChatServiceImpl implements ChatService {
	@Autowired
	private ChatRepository chatRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private AuthContext authContext;
	
	@Autowired
	private WorkerIdProvider workerIdProvider;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	public Chat getChat(long publicId) {
		return chatRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found"));
	}

	@Override
	@Transactional
	public Chat createChat(long publicId, ChatRequest req) {
		long userId = authContext.getCurrentUserId();
		String content = req.getContent();
		ChatType type = req.getType();
		
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		Room room = roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		LocalDateTime now = LocalDateTime.now();
		
		Chat chat = new Chat();
		chat.setContent(content);
		chat.setType(type);
		chat.setPublicId(snowflake.nextId());
		chat.setCreatedAt(now);
		chat.setCreator(user);
		chat.setRoom(room);
		
		return chatRepository.save(chat);
	}

	@Override
	@Transactional
	public Chat updateChat(long publicId, UpdateChatRequest req) {
		String content = req.getContent();
		
		LocalDateTime now = LocalDateTime.now();
		
		Chat chat = chatRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found"));
		
		chat.setContent(content);
		chat.setUpdatedAt(now);
		
		return chatRepository.save(chat);
	}

	@Override
	@Transactional
	public void deleteChat(long publicId) {
		Chat chat = chatRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found"));
		
		chatRepository.delete(chat);
	}
}
