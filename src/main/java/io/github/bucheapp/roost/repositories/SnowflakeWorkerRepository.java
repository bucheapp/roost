package io.github.bucheapp.roost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bucheapp.roost.models.SnowflakeWorker;

public interface SnowflakeWorkerRepository extends JpaRepository<SnowflakeWorker, Long> {
	
}
