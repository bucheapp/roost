package io.github.bucheapp.roost.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.Member;
import io.github.bucheapp.roost.models.User;

public interface MemberRepository extends JpaRepository<Member,Long> {
	Optional<Member> findByCommunityAndUser(Community community,User user);
}
