package io.github.bucheapp.roost.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.CommunityPropertyUpdateRequest;
import io.github.bucheapp.roost.dto.request.CommunityRequest;
import io.github.bucheapp.roost.dto.request.CommunityStateUpdateRequest;
import io.github.bucheapp.roost.dto.request.CommunityUpdateRequest;
import io.github.bucheapp.roost.dto.response.CommunityResponse;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.security.CustomUserDetails;
import io.github.bucheapp.roost.services.CommunityService;

@RestController
@RequestMapping("api/communities")
public class CommunityController {
	@Autowired
	private CommunityService communityService;
	
	@GetMapping("/{publicId}")
	public ResponseEntity<CommunityResponse> getCommunity(
			@PathVariable long publicId
			) {
		Community community = communityService.getCommunityByPublicId(publicId);
		
		CommunityResponse communityResponse = new CommunityResponse(community);
		
		return ResponseEntity.ok(communityResponse);
	}
	
	@PreAuthorize("hasAuthority('CREATE_COMMUNITY')")
	@PostMapping
	public ResponseEntity<CommunityResponse> createCommunity(
			@RequestBody CommunityRequest req,
			Authentication authentication
			) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long id = userDetails.getId();
		
		Community community = communityService.createCommunity(id, req);
		
		CommunityResponse communityResponse = new CommunityResponse(community);
		
		return ResponseEntity.ok(communityResponse);
	}
	
	@PreAuthorize("hasAuthority('UPDATE_COMMUNITY')")
	@PatchMapping("/{publicId}")
	public ResponseEntity<CommunityResponse> updateCommunity(
			@PathVariable long publicId,
			@RequestBody CommunityUpdateRequest req
			) {
		Community community = communityService.updateCommunityByPublicId(publicId, req);
		
		CommunityResponse communityResponse = new CommunityResponse(community);
		
		return ResponseEntity.ok(communityResponse);
	}
	
	@PreAuthorize("hasAuthority('UPDATE_COMMUNITYSTATE')")
	@PatchMapping("/{publicId}/state")
	public ResponseEntity<CommunityResponse> updateCommunityState(
			@PathVariable long publicId,
			@RequestBody CommunityStateUpdateRequest req
			) {
		Community community = communityService.updateCommunityStateByPublicId(publicId, req);
		
		CommunityResponse communityResponse = new CommunityResponse(community);
		
		return ResponseEntity.ok(communityResponse);
	}
	
	@PreAuthorize("hasAuthority('UPDATE_COMMUNITYPROPERTY')")
	@PatchMapping("/{publicId}/property")
	public ResponseEntity<CommunityResponse> updateCommunityProperty(
			@PathVariable long publicId,
			@RequestBody CommunityPropertyUpdateRequest req
			) {
		Community community = communityService.updateCommunityPropertyByPublicId(publicId, req);
		
		CommunityResponse communityResponse = new CommunityResponse(community);
		
		return ResponseEntity.ok(communityResponse);
	}
}
