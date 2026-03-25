package io.github.bucheapp.roost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.Community;

public interface CommunityRepository extends JpaRepository<Community,Long> {

}
