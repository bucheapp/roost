package io.github.bucheapp.roost.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.ChatRequest;
import io.github.bucheapp.roost.dto.request.MemberRequest;
import io.github.bucheapp.roost.dto.request.MemberSearchRequest;
import io.github.bucheapp.roost.models.ApprovalChat;
import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.models.ChatType;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.CommunityProperty;
import io.github.bucheapp.roost.models.CommunityState;
import io.github.bucheapp.roost.models.Member;
import io.github.bucheapp.roost.models.MemberState;
import io.github.bucheapp.roost.models.Room;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.models.UserState;
import io.github.bucheapp.roost.repositories.ChatRepository;
import io.github.bucheapp.roost.repositories.CommunityRepository;
import io.github.bucheapp.roost.repositories.MemberRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.AuthContext;
import io.github.bucheapp.roost.util.MessageUtil;

@Service
public class MemberServiceImpl implements MemberService {
	@Autowired
	private CommunityRepository communityRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private ChatRepository chatRepository;
	
	@Autowired
	private CommunityService communityService;
	
	@Autowired
	private ChatService chatService;
	
	@Autowired
	private MessageUtil messageUtil;
	
	@Autowired
	private AuthContext authContext;
	
	@Override
	@Transactional
	public Set<Member> joinMember(long publicId,MemberRequest req) throws IOException {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));
		
		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("community.notactive"));
		}
		
		Set<Member> members = community.getMembers();
		
		User user = userRepository.findByPublicId(req.getPublicId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		Member member = memberRepository.findByCommunityAndUser(community, user).orElse(null);
		
		if(member == null) {
			if(community.getProperties().contains(CommunityProperty.FREE)) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("access.denied"));
			} else if(community.getProperties().contains(CommunityProperty.OPEN)) {
				Member newMember = new Member(
						MemberState.ACTIVE,
						LocalDateTime.now(),
						user,
						community
						);
				
				members.add(newMember);
			} else {
				Member newMember = new Member(
						MemberState.APPROVING,
						LocalDateTime.now(),
						user,
						community
						);
				
				members.add(newMember);
				
				List<Room> rooms = communityService.getRooms(publicId);
				Room room = rooms.get(0);
				ChatRequest req2 = new ChatRequest();
				req2.setType(ChatType.APPROVAL);
				chatService.createChat(
						room.getPublicId(),
						req2
						);
			}
		} else {
			if(member.getState() == MemberState.BANNED) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.banned"));
			}
			
			if(member.getState() == MemberState.APPROVING) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.approving"));
			}
			
			throw new ResponseStatusException(HttpStatus.CONFLICT,messageUtil.get("member.conflict"));
		}
		
		communityRepository.save(community);
		
		return members;
	}

	@Override
	public Set<Member> getMember(long publicId,MemberSearchRequest req) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));
		
		Set<Member> members = community.getMembers();
		Set<Member> newMembers = new HashSet<>();
		
		for(Member member : members) {
			if(req.getState() == null) {
				if(member.getUser().getState() == UserState.ACTIVE)
					newMembers.add(member);
			} else {
				if(req.getState() == member.getState()) {
					if(member.getUser().getState() == UserState.ACTIVE)
						newMembers.add(member);
				}
			}
		}
		
		return newMembers;
	}

	@Override
	public void leaveMember(long publicId) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));

		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("community.notactive"));
		}
		
		long userId = authContext.getCurrentUserId();

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));

		Member member = memberRepository.findByCommunityAndUser(community, user)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("member.notfound")));

		//ホストは他の人をホストにしない限り脱退できない
		if (community.getHost().getId() == userId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("host.cannot.leave"));
		}
		
		if(member.getState() == MemberState.BANNED) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.banned"));
		}
		
		if(member.getState() == MemberState.APPROVING) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.approving"));
		}

		memberRepository.delete(member);
	}

	@Override
	public void kickMember(long publicId, long userPublicId) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));
		
		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("community.notactive"));
		}
		
		User user = userRepository.findByPublicId(userPublicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		Member member = memberRepository.findByCommunityAndUser(community, user)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("member.notfound")));
		
		if (community.getHost().getPublicId() == userPublicId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("host.cannot.kick"));
		}
		
		if(member.getState() == MemberState.BANNED) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.banned"));
		}
		
		if(member.getState() == MemberState.APPROVING) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.approving"));
		}
		
		memberRepository.delete(member);
	}

	@Override
	public void banMember(long publicId, long userPublicId) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));
		
		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("community.notactive"));
		}
		
		User user = userRepository.findByPublicId(userPublicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		Member member = memberRepository.findByCommunityAndUser(community, user)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("member.notfound")));
		
		if (community.getHost().getPublicId() == userPublicId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("host.cannot.ban"));
		}
		
		if(member.getState() == MemberState.BANNED) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.banned"));
		}
		
		if(member.getState() == MemberState.APPROVING) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.approving"));
		}
		
		member.setState(MemberState.BANNED);
		
		memberRepository.save(member);
	}

	@Override
	public void unbanMember(long publicId, long userPublicId) {
		Community community = communityRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("community.notfound")));
		
		if(community.getState() != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("community.notactive"));
		}
		
		User user = userRepository.findByPublicId(userPublicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		Member member = memberRepository.findByCommunityAndUser(community, user)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("member.notfound")));
		
		if (community.getHost().getPublicId() == userPublicId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("host.cannot.unban"));
		}
		
		if(member.getState() == MemberState.BANNED) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.banned"));
		}
		
		if(member.getState() == MemberState.APPROVING) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.approving"));
		}
		
		if(member.getState() != MemberState.BANNED) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.not.banned"));
		}
		
		member.setState(MemberState.ACTIVE);
		
		memberRepository.save(member);
	}
	
	public void approveMember(long publicId,long userPublicId) {
		long userId = authContext.getCurrentUserId();
		
		Chat chat = chatRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("chat.notfound")));
		
		Room room = chat.getRoom();
		
		Community community = room.getCommunity();
		
		Member approverMember = null;
		Member targetMember = null;
		
		for(Member m : community.getMembers()) {
			if(m.getUser().getId() == userId)
				approverMember = m;
			
			if(m.getUser().getPublicId() == userPublicId)
				targetMember = m;
		}
		
		if(approverMember == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.notjoined"));
		}
		
		if(targetMember == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.notjoined"));
		}
		
		if(targetMember.getState() == MemberState.APPROVING) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("member.notapproving"));
		}
		
		targetMember.setState(MemberState.ACTIVE);
		if(chat instanceof ApprovalChat approvalChat) {
			approvalChat.setApprover(approverMember.getUser());
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,messageUtil.get("not.approvalchat"));
		}
		
		memberRepository.save(targetMember);
	}
}
