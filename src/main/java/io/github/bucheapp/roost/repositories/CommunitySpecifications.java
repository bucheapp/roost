package io.github.bucheapp.roost.repositories;

import java.util.Set;

import jakarta.persistence.criteria.Join;

import org.springframework.data.jpa.domain.Specification;

import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.CommunityProperty;
import io.github.bucheapp.roost.models.CommunityState;
import io.github.bucheapp.roost.models.CommunityType;

public class CommunitySpecifications {

	public static Specification<Community> hasState(CommunityState state) {
		return (root, query, cb) ->
				state == null ? null : cb.equal(root.get("state"), state);
	}

	public static Specification<Community> hasType(CommunityType type) {
		return (root, query, cb) ->
				type == null ? null : cb.equal(root.get("type"), type);
	}

	public static Specification<Community> hasProperties(Set<CommunityProperty> properties) {
		return (root, query, cb) -> {
			if (properties == null || properties.isEmpty()) return null;

			Join<Object, Object> join = root.join("properties");
			return join.in(properties);
		};
	}
	
	public static Specification<Community> hasName(String name) {
		return (root, query, cb) -> {
			if (name == null || name.isEmpty()) return null;

			return cb.like(root.get("name"), "%" + name + "%");
		};
	}
}
