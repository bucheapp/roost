package io.github.bucheapp.roost.services;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.github.bucheapp.roost.models.SnowflakeWorker;
import io.github.bucheapp.roost.repositories.SnowflakeWorkerRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class WorkerIdProvider {
	
	private final long workerId;
	
	private final ObjectMapper mapper = new ObjectMapper();

	public WorkerIdProvider(
			@Value("${app.data-dir}") String dataDirPath
			,SnowflakeWorkerRepository repository) {
		File dataDir = new File(dataDirPath);
		dataDir.mkdirs();
		
		File settingsFile = new File(dataDir,"settings.json");
		if(!settingsFile.exists()) {
			mapper.writeValue(settingsFile, new HashMap<>());
		}
		
		Map<String, Object> jsonMap = mapper.readValue(settingsFile,
				new TypeReference<Map<String, Object>>() {});
		
		Integer workerIdValue = (Integer) jsonMap.get("workerId");
		
		if(workerIdValue == null) {
			this.workerId = repository.save(new SnowflakeWorker()).getId();
			jsonMap.put("workerId", this.workerId);
			
			mapper.writeValue(settingsFile, jsonMap);
		} else {
			this.workerId = workerIdValue;
		}
	}

	public long getWorkerId() {
		return workerId;
	}
}
