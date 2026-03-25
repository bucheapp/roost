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
	
	@ElementCollection(targetClass = CommunityStatus.class)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "community_statuses", joinColumns = @JoinColumn(name = "community_id"))
	@Column(name = "status")
	private Set<CommunityStatus> statuses = new HashSet<>();
    
	@Column(unique = true)
	private long publicId;
	
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@Column(updatable = false)
	private LocalDateTime archivedAt;
	
	@ManyToOne
	@JoinColumn(name = "creator_id")
	private User creator;
	
	@Column
	boolean frozen;
	
	@Column
	boolean archived;
}
