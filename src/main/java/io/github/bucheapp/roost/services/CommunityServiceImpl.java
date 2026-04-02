package io.github.bucheapp.roost.services;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.CommunityRepository;
import io.github.bucheapp.roost.repositories.MemberRepository;
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
	private MemberRepository memberRepository;

	@Autowired
	private WorkerIdProvider workerIdProvider;

	@Autowired
	private AuthContext authContext;

	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;

	@Override
	public Community getCommunity(long publicId) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

		return communityRepository.save(community);
	}

	//TODO: Pageableで検索する
	@Override
	public List<Community> search(CommunitySearchRequest req) {
		List<Community> communities;

		if (req.getProperties() != null && !req.getProperties().isEmpty()) {
			communities = communityRepository.findByPropertiesIn(req.getProperties());
		} else {
			communities = communityRepository.findAll();
		}

		if ("NEW".equals(req.getSort())) {
			communities.sort(Comparator.comparing(Community::getCreatedAt).reversed());
		} else if ("OLD".equals(req.getSort())) {
			communities.sort(Comparator.comparing(Community::getCreatedAt));
		}

		int from = req.getPage() * req.getSize();
		int to = Math.min(from + req.getSize(), communities.size());

		return communities.subList(from, to);
	}

	public void assignmentHost(long publicId, long userPublicId) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

		User user = userRepository.findByPublicId(userPublicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		Member member = memberRepository.findByCommunityAndUser(community, user)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

		community.addHostHistory(member.getUser());
		community.checkStateActive();
		
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
			if (community.getState() != CommunityState.ARCHIVED) {
				new ResponseStatusException(HttpStatus.CONFLICT, "A community with that name already exists");
				break;
			}
		}

		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		LocalDateTime now = LocalDateTime.now();

		Community newCommunity = new Community();
		newCommunity.setName(name);
		newCommunity.setType(type);
		newCommunity.setState(CommunityState.ACTIVE);
		newCommunity.setPublicId(snowflake.nextId());
		newCommunity.setCreatedAt(now);
		newCommunity.addHostHistory(user);
		newCommunity.setArchiveAt(
				LocalDateTime.now().plusDays(7));

		Member member = new Member();
		member.setUser(user);
		member.setTime(LocalDateTime.now());
		newCommunity.addMember(member);

		return communityRepository.save(newCommunity);
	}

	@Override
	@Transactional
	public Community updateCommunity(long publicId, UpdateCommunityRequest req) {
		String name = req.getName();
		CommunityType type = req.getType();

		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
		
		community.archiveIfNeeded();
		
		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Inactive communities cannot be edited");
		}

		LocalDateTime now = LocalDateTime.now();

		community.setName(name);
		community.setType(type);
		community.setUpdatedAt(now);
		community.checkStateActive();

		return communityRepository.save(community);
	}

	@Override
	@Transactional
	public Community updateCommunityState(long publicId, UpdateCommunityStateRequest req) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

		LocalDateTime now = LocalDateTime.now();
		community.setState(req.getState());
		community.setUpdatedAt(now);

		return communityRepository.save(community);
	}

	@Override
	@Transactional
	public Community updateCommunityProperty(long publicId, UpdateCommunityPropertyRequest req) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

		LocalDateTime now = LocalDateTime.now();
		community.setProperties(req.getProperties());
		community.setUpdatedAt(now);

		return communityRepository.save(community);
	}
}
