package io.github.bucheapp.roost.services;

import java.time.LocalDateTime;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.github.bucheapp.roost.dto.request.ChatRequest;
import io.github.bucheapp.roost.dto.request.ChatUpdateRequest;
import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.models.ChatType;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.ChatRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.util.Snowflake;

public class ChatServiceImpl implements ChatService {
	@Autowired
	private ChatRepository chatRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WorkerIdProvider workerIdProvider;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	public Chat getChatByPublicId(long publicId) {
		return chatRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("チャットが見つかりません"));
	}

	@Override
	@Transactional
	public Chat createChat(long id,long publicId, ChatRequest req) {
		String content = req.getContent();
		ChatType type = req.getType();
		
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		LocalDateTime now = LocalDateTime.now();
		
		Chat chat = new Chat();
		chat.setContent(content);
		chat.setType(type);
		chat.setPublicId(snowflake.nextId());
		chat.setCreatedAt(now);
		chat.setCreator(user);
		
		return chatRepository.save(chat);
	}

	@Override
	@Transactional
	public Chat updateChatByPublicId(long publicId, ChatUpdateRequest req) {
		String content = req.getContent();
		
		LocalDateTime now = LocalDateTime.now();
		
		Chat chat = new Chat();
		chat.setContent(content);
		chat.setUpdatedAt(now);
		
		return chatRepository.save(chat);
	}

	@Override
	@Transactional
	public void deleteChatByPublicId(long publicId) {
		Chat chat = chatRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("チャットが見つかりません"));
		
		chatRepository.delete(chat);
	}
}
