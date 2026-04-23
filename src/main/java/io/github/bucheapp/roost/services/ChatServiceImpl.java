package io.github.bucheapp.roost.services;

import java.time.LocalDateTime;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.ChatRequest;
import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.models.ChatType;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.Room;
import io.github.bucheapp.roost.models.TextChat;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.ChatRepository;
import io.github.bucheapp.roost.repositories.RoomRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.AuthContext;
import io.github.bucheapp.roost.util.MessageUtil;
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
	
	@Autowired
	private MessageUtil messageUtil;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	public Chat getChat(long publicId) {
		return chatRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("chat.notfound")));
	}

	@Override
	@Transactional
	public Chat createChat(long publicId, ChatRequest req) {
		long userId = authContext.getCurrentUserId();
		ChatType type = req.getType();
		
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		Room room = roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		LocalDateTime now = LocalDateTime.now();
		
		Chat chat = null;
		
		if(type == ChatType.TEXT) {
			MultipartFile file = req.getFile();
			if(file != null) {
				checkByte(req.getFile());
				//TODO: ファイルを作成するロジックを作成
			}
			chat = new TextChat(
					snowflake.nextId(),
					LocalDateTime.now(),
					user,
					room,
					req
					);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageUtil.get("unknown.chattype"));
		}
		
		chat.setPublicId(snowflake.nextId());
		chat.setCreatedAt(now);
		chat.setRoom(room);
		chat.setCreator(user);
		
		return chatRepository.save(chat);
	}

	@Override
	@Transactional
	public Chat updateChat(long publicId, ChatRequest req) {
		ChatType type = req.getType();
		long userId = authContext.getCurrentUserId();
		
		LocalDateTime now = LocalDateTime.now();
		
		Chat chat = chatRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("chat.notfound")));
		
		User creator = chat.getCreator();
		
		if(creator.getId() != userId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("cannot.edit.chat"));
		}
		
		if(type == ChatType.TEXT) {
			MultipartFile file = req.getFile();
			TextChat textChat = (TextChat) chat;
			textChat.setContent(req.getContent());
			if(file != null) {
				checkByte(req.getFile());
				//TODO: ファイルを作成するロジックを作成
			}
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageUtil.get("unknown.chattype"));
		}
		
		chat.setUpdatedAt(now);
		
		Room room = chat.getRoom();
		Community community = room.getCommunity();
		community.checkStateActive();
		
		return chatRepository.save(chat);
	}

	@Override
	@Transactional
	public void deleteChat(long publicId) {
		long userId = authContext.getCurrentUserId();
		
		Chat chat = chatRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("chat.notfound")));
		
		Room room = chat.getRoom();
		Community community = room.getCommunity();
		
		User creator = chat.getCreator();
		
		if(creator.getId() != userId || 
				!authContext.hasAuthority("DELETE_CHAT") ||
				community.getHost().getId() != userId
				) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("cannot.delete.chat"));
		}
		
		community.checkStateActive();
		
		chatRepository.delete(chat);
	}

	@Override
	public void checkByte(MultipartFile file) {
		if(file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageUtil.get("file.isempty"));
		}
		
		if (file.getSize() > 10 * 1024 * 1024) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageUtil.get("file.size.exceeds"));
		}
	}
}
