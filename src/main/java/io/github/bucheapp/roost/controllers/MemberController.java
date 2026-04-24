package io.github.bucheapp.roost.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.MemberRequest;
import io.github.bucheapp.roost.dto.request.MemberSearchRequest;
import io.github.bucheapp.roost.dto.response.MemberResponse;
import io.github.bucheapp.roost.dto.response.sw.MemberSWResponse;
import io.github.bucheapp.roost.models.Member;
import io.github.bucheapp.roost.models.MemberState;
import io.github.bucheapp.roost.models.SWType;
import io.github.bucheapp.roost.services.MemberService;

@RestController
public class MemberController {
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@PostMapping("api/communities/{publicId}/members")
	public ResponseEntity<Set<MemberResponse>> joinMember(
			@PathVariable long publicId,
			@RequestBody MemberRequest req
			) throws IOException {
		Set<Member> members = memberService.joinMember(publicId,req);
		
		Set<MemberResponse> memberResponses = members.stream()
				.map(MemberResponse::new)
				.collect(Collectors.toSet());
		
		MemberSWResponse memberSWResponse = new MemberSWResponse(members,SWType.NEW);
		template.convertAndSend("/topic/community/" + publicId + "/member", memberSWResponse);
		
		return ResponseEntity.ok(memberResponses);
	}
	
	@GetMapping("api/communities/{publicId}/members")
	public ResponseEntity<Set<MemberResponse>> getMembers(
			@PathVariable long publicId,
			@RequestParam(required = false) MemberState state
			) {
		
		MemberSearchRequest req = new MemberSearchRequest(state);
		
		Set<Member> members = memberService.getMember(publicId,req);
		
		Set<MemberResponse> memberResponses = members.stream()
				.map(MemberResponse::new)
				.collect(Collectors.toSet());
		
		return ResponseEntity.ok(memberResponses);
	}
	
	@DeleteMapping("api/communities/{publicId}/members")
	public ResponseEntity<Void> leaveMember(
			@PathVariable long publicId
			) {
		
		memberService.leaveMember(publicId);
		
		Map<String, Object> payload = Map.of(
				"publicId", publicId,
				"swType", SWType.DELETE
			);
		
		template.convertAndSend("/topic/community/" + publicId + "/member", (Object) payload);
		
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("api/communities/{publicId}/members/{userPublicId}/kick")
	@PreAuthorize(
		"hasAuthority('KICK_MEMBER') or @communitySecurity.isHost(#publicId)"
	)
	public ResponseEntity<Void> kickMember(
		@PathVariable long publicId,
		@PathVariable long userPublicId
	) {
		memberService.kickMember(publicId, userPublicId);
		
		Map<String, Object> payload = Map.of(
				"publicId", userPublicId,
				"communityId",publicId,
				"swType", SWType.DELETE
			);
		
		template.convertAndSend("/topic/community/" + publicId + "/member", (Object) payload);
		
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("api/communities/{publicId}/members/{userPublicId}/ban")
	@PreAuthorize(
		"hasAuthority('BAN_MEMBER') or @communitySecurity.isHost(#publicId)"
	)
	public ResponseEntity<Void> banMember(
		@PathVariable long publicId,
		@PathVariable long userPublicId
	) {
		memberService.banMember(publicId, userPublicId);
		
		return ResponseEntity.noContent().build();
	}
}
