package io.github.bucheapp.roost.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Entity
@Table(name = "communities")
public class Community {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private long id;
	
	@Column(length = 30)
	@NotBlank
	@Size(min = 3,max = 30)
	@Pattern(regexp = "^[\\p{L}]+$")
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
	private Set<CommunityProperty> properties;
	
	@Column(unique = true)
	private long publicId;
	
	@Column(updatable = false)
	@NotNull
	private LocalDateTime createdAt;
	
	@Column
	private LocalDateTime updatedAt;
	
	@Column
	private LocalDateTime archiveAt;
	
	@OneToMany(mappedBy = "community", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<HostHistory> hostHistory;
	
	@OneToMany(mappedBy = "community", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Member> members;
	
	public Community() {
		this.properties = new HashSet<>();
		this.hostHistory = new ArrayList<>();
		this.members = new HashSet<>();
	}

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

	public LocalDateTime getArchiveAt() {
		return archiveAt;
	}

	public void setArchiveAt(LocalDateTime archiveAt) {
		this.archiveAt = archiveAt;
	}

	public List<HostHistory> getOperatorHistory() {
		return hostHistory;
	}

	public void setHostHistory(List<HostHistory> hostHistory) {
		this.hostHistory = hostHistory;
	}
	
	public void addHostHistory(User user) {
		hostHistory.add(new HostHistory(
				user,
				LocalDateTime.now()
				));
	}

	public Set<Member> getMembers() {
		return members;
	}

	public void setMembers(Set<Member> members) {
		this.members = members;
	}
	
	public void addMember(Member member) {
		this.members.add(member);
	}
	
	public void removeMember(Member member) {
		this.members.remove(member);
	}
	
	public User getHost() {
		return this.hostHistory.getLast().getUser();
	}
	
	public void archiveIfNeeded() {
		if(archiveAt != null && LocalDateTime.now().isAfter(archiveAt) && !(state == CommunityState.FROZEN)) {
			state = CommunityState.ARCHIVED;
		}
	}
	
	public void checkStateActive() {
		if(state != CommunityState.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The community is not active");
		}
	}
	
	@PreUpdate
	@PrePersist
	public void preUpdate() {
		archiveIfNeeded();
	}
}
