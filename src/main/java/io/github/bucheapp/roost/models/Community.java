package io.github.bucheapp.roost.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "communities")
public class Community {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private long id;
	
	@Column
	private String name;
	
	@Column
	@Enumerated(EnumType.STRING)
	private CommunityType type;
	
	@Column
	@Enumerated(EnumType.STRING)
	private CommunityState state;
	
	@ElementCollection(targetClass = CommunityProperty.class)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "community_properties", joinColumns = @JoinColumn(name = "community_id"))
	@Column
	private Set<CommunityProperty> properties = new HashSet<>();
	
	@Column(unique = true)
	private long publicId;
	
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@Column
	private LocalDateTime updatedAt;
	
	@Column(updatable = false)
	private LocalDateTime archivedAt;
	
	@ManyToOne
	@JoinColumn(name = "creator_id")
	private User creator;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CommunityType getType() {
		return type;
	}

	public void setType(CommunityType type) {
		this.type = type;
	}

	public CommunityState getState() {
		return state;
	}

	public void setState(CommunityState state) {
		this.state = state;
	}

	public Set<CommunityProperty> getProperties() {
		return properties;
	}

	public void setProperties(Set<CommunityProperty> properties) {
		this.properties = properties;
	}

	public long getPublicId() {
		return publicId;
	}

	public void setPublicId(long publicId) {
		this.publicId = publicId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDateTime getArchivedAt() {
		return archivedAt;
	}

	public void setArchivedAt(LocalDateTime archivedAt) {
		this.archivedAt = archivedAt;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}
}
