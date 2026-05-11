package io.github.bucheapp.roost.services;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.ChatRequest;
import io.github.bucheapp.roost.models.ApprovalChat;
import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.models.ChatType;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.CommunityState;
import io.github.bucheapp.roost.models.MediaContent;
import io.github.bucheapp.roost.models.MediaType;
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
	private FileService fileService;
	
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
	public Chat createChat(long publicId, ChatRequest req) throws IOException {
		long userId = authContext.getCurrentUserId();
		ChatType type = req.getType();
		
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		Room room = roomRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		
		Chat chat = null;
		
		if(type == ChatType.TEXT) {
			MultipartFile file = req.getFile();
			String url = null;
			if(file != null) {
				fileService.checkByte(file, 10 * 1024 * 1024);
				
				if(req.getMediaType() == MediaType.IMAGE) {
					url = fileService.createImage("image",file);
				} else if(req.getMediaType() == MediaType.VIDEO) {
					url = fileService.createVideo("video",file);
				} else if(req.getMediaType() == MediaType.AUDIO) {
					url = fileService.createAudio("audio",file);
				}
			}
			
			chat = new TextChat(
					snowflake.nextId(),
					LocalDateTime.now(),
					user,
					room,
					req
					);
			
			if(chat instanceof TextChat textChat) {
				textChat.setMediaContent(
						new MediaContent(
								url,
								req.getMediaType()
								)
						);
			}
		} else if(type == ChatType.APPROVAL) {
			chat = new ApprovalChat(
					snowflake.nextId(),
					LocalDateTime.now(),
					user,
					room
					);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageUtil.get("unknown.chattype"));
		}
		
		return chatRepository.save(chat);
	}

	@Override
	@Transactional
	public Chat updateChat(long publicId, ChatRequest req) throws IOException {
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
			String url = null;
			if(file != null) {
				fileService.checkByte(file, 10 * 1024 * 1024);
				
				if(req.getMediaType() == MediaType.IMAGE) {
					if(textChat.getMediaContent() != null)
							fileService.deleteFile("image");
					url = fileService.createImage("image",file);
				} else if(req.getMediaType() == MediaType.VIDEO) {
					url = fileService.createVideo("video",file);
				} else if(req.getMediaType() == MediaType.AUDIO) {
					url = fileService.createAudio("audio",file);
				}
			}
			
			textChat.setMediaContent(
					new MediaContent(
							url,
							req.getMediaType()
							)
					);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageUtil.get("unknown.chattype"));
		}
		
		chat.setUpdatedAt(now);
		
		Room room = chat.getRoom();
		Community community = room.getCommunity();
		
		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("community.notactive"));
		}
		
		return chatRepository.save(chat);
	}

	@Override
	@Transactional
	public void deleteChat(long publicId) {
		long userId = authContext.getCurrentUserId();
		
		Chat chat = chatRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("chat.notfound")));
		
		if(chat instanceof TextChat textChat) {
			fileService.deleteFile("image/" + textChat.getMediaContent().getMediaContentUrl());
		}
		
		Room room = chat.getRoom();
		Community community = room.getCommunity();
		
		User creator = chat.getCreator();
		
		if(creator.getId() != userId || 
				!authContext.hasAuthority("DELETE_CHAT") ||
				community.getHost().getId() != userId
				) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("cannot.delete.chat"));
		}
		
		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("community.notactive"));
		}
		
		chatRepository.delete(chat);
	}
}
