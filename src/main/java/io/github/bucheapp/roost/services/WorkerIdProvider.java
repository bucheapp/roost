package io.github.bucheapp.roost.services;

import org.springframework.stereotype.Component;

import io.github.bucheapp.roost.models.SnowflakeWorker;
import io.github.bucheapp.roost.repositories.SnowflakeWorkerRepository;

@Component
public class WorkerIdProvider {
	private final long workerId;

	public WorkerIdProvider(SnowflakeWorkerRepository repository) {
		this.workerId = repository.save(new SnowflakeWorker()).getId();
	}

	public long getWorkerId() {
		return workerId;
	}
}
