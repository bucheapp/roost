package io.github.bucheapp.roost.controllers;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.MemberRequest;
import io.github.bucheapp.roost.dto.response.MemberResponse;
import io.github.bucheapp.roost.models.Member;
import io.github.bucheapp.roost.services.MemberService;

@RestController
public class MemberController {
	@Autowired
	private MemberService memberService;
	
	@PostMapping("api/communities/{publicId}/members")
	public ResponseEntity<Set<MemberResponse>> joinMember(
			@PathVariable long publicId,
			@RequestBody MemberRequest req
			) {
		Set<Member> members = memberService.joinMember(publicId,req);
		
		Set<MemberResponse> memberResponses = members.stream()
				.map(MemberResponse::new)
				.collect(Collectors.toSet());
		
		return ResponseEntity.ok(memberResponses);
	}
	
	@GetMapping("api/communities/{publicId}/members")
	public ResponseEntity<Set<MemberResponse>> getMembers(
			@PathVariable long publicId
			) {
		Set<Member> members = memberService.getMember(publicId);
		
		Set<MemberResponse> memberResponses = members.stream()
				.map(MemberResponse::new)
				.collect(Collectors.toSet());
		
		return ResponseEntity.ok(memberResponses);
	}
	
	@DeleteMapping("api/communities/{publicId}/members")
	public ResponseEntity<Set<MemberResponse>> leaveMember(
			@PathVariable long publicId
			) {
		
		memberService.leaveMember(publicId);
		
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("api/communities/{publicId}/members/{userPublicId}")
	@PreAuthorize(
		"hasAuthority('KICK_MEMBER') or @communitySecurity.isHost(#publicId)"
	)
	public ResponseEntity<Void> kickMember(
		@PathVariable long publicId,
		@PathVariable long userPublicId
	) {
		memberService.kickMember(publicId, userPublicId);
		
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("api/communities/{publicId}/members/{userPublicId}")
	@PreAuthorize(
		"hasAuthority('BAN_MEMBER') or @communitySecurity.isHost(#publicId)"
	)
	public ResponseEntity<Void> banMember(
		@PathVariable long publicId,
		@PathVariable long userPublicId
	) {
		memberService.kickMember(publicId, userPublicId);
		return ResponseEntity.noContent().build();
	}
}
