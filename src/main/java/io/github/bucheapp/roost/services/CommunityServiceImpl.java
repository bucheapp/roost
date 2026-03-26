package io.github.bucheapp.roost.services;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.bucheapp.roost.dto.request.CommunityRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityPropertyRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityStateRequest;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.CommunityState;
import io.github.bucheapp.roost.models.CommunityType;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.CommunityRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.AuthContext;
import io.github.bucheapp.roost.util.Snowflake;

@Service
public class CommunityServiceImpl implements CommunityService {
	@Autowired
	private CommunityRepository communityRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WorkerIdProvider workerIdProvider;
	
	@Autowired
	private AuthContext authContext;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	public Community getCommunity(long publicId) {
		return communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("コミュニティが見つかりません"));
	}

	@Override
	@Transactional
	public Community createCommunity(CommunityRequest req) {
		long userId = authContext.getCurrentUserId();
		
		String name = req.getName();
		CommunityType type = req.getType();
		
		List<Community> communityList = communityRepository.findByName(name);
		
		for(Community community : communityList) {
			if(community.getState() != CommunityState.ARCHIVED) {
				new RuntimeException("同じ名前のコミュニティが既に存在しています");
				break;
			}
		}
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
		
		LocalDateTime now = LocalDateTime.now();
		
		Community newCommunity = new Community();
		newCommunity.setName(name);
		newCommunity.setType(type);
		newCommunity.setState(CommunityState.ACTIVE);
		newCommunity.setPublicId(snowflake.nextId());
		newCommunity.setCreatedAt(now);
		newCommunity.setCreator(user);
		
		return communityRepository.save(newCommunity);
	}

	@Override
	@Transactional
	public Community updateCommunity(long publicId, UpdateCommunityRequest req) {
		String name = req.getName();
		CommunityType type = req.getType();
		
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("コミュニティが見つかりません"));
		
		LocalDateTime now = LocalDateTime.now();
		
		community.setName(name);
		community.setType(type);
		community.setUpdatedAt(now);
		
		return communityRepository.save(community);
	}

	@Override
	@Transactional
	public Community updateCommunityState(long publicId, UpdateCommunityStateRequest req) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("コミュニティが見つかりません"));
		
		LocalDateTime now = LocalDateTime.now();
		community.setState(req.getState());
		community.setUpdatedAt(now);
		
		return communityRepository.save(community);
	}

	@Override
	@Transactional
	public Community updateCommunityProperty(long publicId, UpdateCommunityPropertyRequest req) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("コミュニティが見つかりません"));
		
		LocalDateTime now = LocalDateTime.now();
		community.setProperties(req.getProperties());
		community.setUpdatedAt(now);
		
		return communityRepository.save(community);
	}
}
