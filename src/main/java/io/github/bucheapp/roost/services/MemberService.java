package io.github.bucheapp.roost.services;

import java.util.Set;

import io.github.bucheapp.roost.dto.request.MemberRequest;
import io.github.bucheapp.roost.models.Member;

public interface MemberService {
	Set<Member> joinMember(long publicId,MemberRequest req);
	Set<Member> getMember(long publicId);
	Set<Member> leaveMember(long publicId,MemberRequest req);
}
