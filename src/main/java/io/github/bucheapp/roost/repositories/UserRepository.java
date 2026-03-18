package io.github.bucheapp.roost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.bucheapp.roost.models.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	boolean existsByMail(String mail);
}
