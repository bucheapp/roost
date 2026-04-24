package io.github.bucheapp.roost.services;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.CommunityRequest;
import io.github.bucheapp.roost.dto.request.CommunitySearchRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityPropertyRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityStateRequest;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.CommunityState;
import io.github.bucheapp.roost.models.CommunityType;
import io.github.bucheapp.roost.models.Member;
import io.github.bucheapp.roost.models.MemberState;
import io.github.bucheapp.roost.models.Room;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.CommunityRepository;
import io.github.bucheapp.roost.repositories.CommunitySpecifications;
import io.github.bucheapp.roost.repositories.MemberRepository;
import io.github.bucheapp.roost.repositories.RoomRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.AuthContext;
import io.github.bucheapp.roost.util.MessageUtil;
import io.github.bucheapp.roost.util.Snowflake;

@Service
public class CommunityServiceImpl implements CommunityService {
	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private WorkerIdProvider workerIdProvider;

	@Autowired
	private AuthContext authContext;
	
	@Autowired
	private MessageUtil messageUtil;

	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;

	@Override
	public Community getCommunity(long publicId) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));

		return communityRepository.save(community);
	}
	
	@Override
	public List<Room> getRooms(long publicId) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));
		
		List<Room> rooms = roomRepository.findByCommunityId(community.getId());
		
		return rooms;
	}
	
	@Override
	public List<Community> search(
			CommunitySearchRequest req,
			Pageable pageable
			) {
		
		Specification<Community> spec = Specification
				.where(CommunitySpecifications.hasState(req.getState()))
				.and(CommunitySpecifications.hasType(req.getType()))
				.and(CommunitySpecifications.hasProperties(req.getProperties())
				.and(CommunitySpecifications.hasName(req.getName())));
		
		Pageable newPageable = PageRequest.of(Math.max(pageable.getPageNumber(),0),Math.min(pageable.getPageSize(),10),pageable.getSort());
		
		return communityRepository.findAll(spec,newPageable).getContent();
	}

	public void assignmentHost(long publicId, long userPublicId) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));
		
		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("community.notactive"));
		}

		User user = userRepository.findByPublicId(userPublicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));

		Member member = memberRepository.findByCommunityAndUser(community, user)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("member.notfound")));

		community.addHostHistory(member.getUser());
		
		communityRepository.save(community);
	}

	@Override
	@Transactional
	public Community createCommunity(CommunityRequest req) {
		long userId = authContext.getCurrentUserId();

		String name = req.getName();
		CommunityType type = req.getType();

		List<Community> communityList = communityRepository.findByName(name);

		for (Community community : communityList) {
			if (community.getState() == CommunityState.ACTIVE) {
				new ResponseStatusException(HttpStatus.CONFLICT, messageUtil.get("community.name.conflict"));
				break;
			}
		}

		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));

		Community newCommunity = new Community(
				snowflake.nextId(),
				name,
				type,
				CommunityState.ACTIVE,
				LocalDateTime.now()
				);
		
		newCommunity.addHostHistory(user);
		newCommunity.setArchiveAt(
				LocalDateTime.now().plusDays(7));

		Member member = new Member(
				MemberState.ACTIVE,
				LocalDateTime.now(),
				user,
				newCommunity
				);
		
		newCommunity.addMember(member);

		return communityRepository.save(newCommunity);
	}

	@Override
	@Transactional
	public Community updateCommunity(long publicId, UpdateCommunityRequest req) {
		String name = req.getName();
		CommunityType type = req.getType();

		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));
		
		community.archiveIfNeeded();
		
		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("community.notactive"));
		}

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
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));

		LocalDateTime now = LocalDateTime.now();
		community.setState(req.getState());
		community.setUpdatedAt(now);

		return communityRepository.save(community);
	}

	@Override
	@Transactional
	public Community updateCommunityProperty(long publicId, UpdateCommunityPropertyRequest req) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));

		LocalDateTime now = LocalDateTime.now();
		community.setProperties(req.getProperties());
		community.setUpdatedAt(now);

		return communityRepository.save(community);
	}
}
