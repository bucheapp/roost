package io.github.bucheapp.roost.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.CommunityRequest;
import io.github.bucheapp.roost.dto.request.CommunitySearchRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityPropertyRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityStateRequest;
import io.github.bucheapp.roost.dto.response.CommunityResponse;
import io.github.bucheapp.roost.dto.response.CommunitySWResponse;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.SWType;
import io.github.bucheapp.roost.services.CommunityService;

@RestController
@RequestMapping("api/communities")
public class CommunityController {
	@Autowired
	private CommunityService communityService;
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@GetMapping("/{publicId}")
	public ResponseEntity<CommunityResponse> getCommunity(
			@PathVariable long publicId
			) {
		Community community = communityService.getCommunity(publicId);
		
		CommunityResponse communityResponse = new CommunityResponse(community);
		
		return ResponseEntity.ok(communityResponse);
	}
	
	@GetMapping("/api/communities")
	public ResponseEntity<List<CommunityResponse>> getCommunities(
		CommunitySearchRequest req
	) {
		List<Community> communities = communityService.search(req);
		List<CommunityResponse> communityResponses = communities.stream()
				.map(CommunityResponse::new)
				.toList();

		return ResponseEntity.ok(communityResponses);
	}
	
	@PreAuthorize("hasAuthority('CREATE_COMMUNITY')")
	@PostMapping
	public ResponseEntity<CommunityResponse> createCommunity(
			@RequestBody CommunityRequest req
			) {
		
		Community community = communityService.createCommunity(req);
		
		CommunityResponse communityResponse = new CommunityResponse(community);
		
		CommunitySWResponse communitySWResponse = new CommunitySWResponse(community,SWType.NEW);
		template.convertAndSend("/topic/community/global", communitySWResponse);
		
		return ResponseEntity.ok(communityResponse);
	}
	
	@PreAuthorize("hasAuthority('UPDATE_COMMUNITY')")
	@PatchMapping("/{publicId}")
	public ResponseEntity<CommunityResponse> updateCommunity(
			@PathVariable long publicId,
			@RequestBody UpdateCommunityRequest req
			) {
		Community community = communityService.updateCommunity(publicId, req);
		
		CommunityResponse communityResponse = new CommunityResponse(community);
		
		CommunitySWResponse communitySWResponse = new CommunitySWResponse(community,SWType.UPDATE);
		template.convertAndSend("/topic/community/global", communitySWResponse);
		
		return ResponseEntity.ok(communityResponse);
	}
	
	@PreAuthorize("hasAuthority('UPDATE_COMMUNITYSTATE') or @communitySecurity.isHost(#publicId)")
	@PatchMapping("/{publicId}/state")
	public ResponseEntity<CommunityResponse> updateCommunityState(
			@PathVariable long publicId,
			@RequestBody UpdateCommunityStateRequest req
			) {
		Community community = communityService.updateCommunityState(publicId, req);
		
		CommunityResponse communityResponse = new CommunityResponse(community);
		
		return ResponseEntity.ok(communityResponse);
	}
	
	@PreAuthorize("hasAuthority('UPDATE_COMMUNITYPROPERTY')")
	@PatchMapping("/{publicId}/property")
	public ResponseEntity<CommunityResponse> updateCommunityProperty(
			@PathVariable long publicId,
			@RequestBody UpdateCommunityPropertyRequest req
			) {
		Community community = communityService.updateCommunityProperty(publicId, req);
		
		CommunityResponse communityResponse = new CommunityResponse(community);
		
		return ResponseEntity.ok(communityResponse);
	}
	
	@PostMapping("api/communities/{publicId}/host/{userPublicId}")
	public ResponseEntity<Void> assignmentHost(
			@PathVariable long publicId,
			@PathVariable long userPublicId
			) {
		communityService.assignmentHost(publicId, userPublicId);
		
		return ResponseEntity.noContent().build();
	}
}
