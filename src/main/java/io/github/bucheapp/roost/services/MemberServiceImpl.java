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
import io.github.bucheapp.roost.repositories.MemberRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.AuthContext;

@Service
public class MemberServiceImpl implements MemberService {
	@Autowired
	private CommunityRepository communityRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
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
			member.setCommunity(community);
			
			members.add(member);
		} else {
			//TODO: 参加するためにメンバーからの認証を求めるシステムを作成
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
	public void leaveMember(long publicId) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

		long userId = authContext.getCurrentUserId();

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		Member member = memberRepository.findByCommunityAndUser(community, user)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

		if (community.getHost().getId() == userId) {
			//TODO: ホストの場合は別の処理を追加
		}

		memberRepository.delete(member);
	}

	@Override
	public void kickMember(long publicId, long userPublicId) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
		
		User user = userRepository.findByPublicId(userPublicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		Member member = memberRepository.findByCommunityAndUser(community, user)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
		
		//ホストはキックできない
		if (community.getHost().getPublicId() == userPublicId) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't kick");
		}
		
		memberRepository.delete(member);
	}

	@Override
	public void banMember(long publicId, long userPubliId) {
		//TODO: BANのロジック作成
	}
}
