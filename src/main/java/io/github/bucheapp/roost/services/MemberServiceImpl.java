package io.github.bucheapp.roost.services;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.MemberRequest;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.CommunityProperty;
import io.github.bucheapp.roost.models.Member;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.CommunityRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.AuthContext;

@Service
public class MemberServiceImpl implements MemberService {
	@Autowired
	private CommunityRepository communityRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthContext authContext;
	
	@Override
	@Transactional
	public Set<Member> joinMember(long publicId,MemberRequest req) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
		
		Set<Member> members = community.getMembers();
		
		User user = userRepository.findByPublicId(req.getPublicId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		if(community.getProperties().contains(CommunityProperty.OPEN)) {
			Member member = new Member();
			member.setUser(user);
			member.setTime(LocalDateTime.now());
			
			members.add(member);
		} else {
			//TODO 参加するためにメンバーからの認証を求めるシステムを作成
		}
		
		communityRepository.save(community);
		
		return members;
	}

	@Override
	public Set<Member> getMember(long publicId) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
		
		Set<Member> members = community.getMembers();
		
		return members;
	}

	@Override
	public Set<Member> leaveMember(long publicId, MemberRequest req) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
		
		Set<Member> members = community.getMembers();
		
		long userId = authContext.getCurrentUserId();
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		if(user.getPublicId() != req.getPublicId()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot leave the user");
		}
		
		communityRepository.save(community);
		
		return members;
	}
}
