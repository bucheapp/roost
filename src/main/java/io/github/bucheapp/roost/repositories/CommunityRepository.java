package io.github.bucheapp.roost.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.CommunityProperty;

public interface CommunityRepository extends JpaRepository<Community,Long>,JpaSpecificationExecutor<Community> {
	Optional<Community> findByPublicId(long publicId);
	List<Community> findByName(String name);
	List<Community> findByPropertiesIn(Set<CommunityProperty> properties);
	Page<Community> findDistinctByMembers_User_Id(
			Long userId,
			Pageable pageable
		);
}
