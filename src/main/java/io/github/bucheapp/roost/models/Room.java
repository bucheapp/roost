package io.github.bucheapp.roost.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "rooms")
public class Room {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private long id;
	
	@Column(length = 10)
	@NotBlank
	@Size(min = 3,max = 10)
	@Pattern(regexp = "^[\\p{L}]+$")
	private String name;
	
	@Column(unique = true)
	private long publicId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@NotNull
	private Community community;

	@Column(updatable = false)
	@NotNull
	private LocalDateTime createdAt;
	
	@Column
	private LocalDateTime updatedAt;
	
	@ManyToOne
	@JoinColumn(name = "creator_id")
	@NotNull
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

	public long getPublicId() {
		return publicId;
	}

	public void setPublicId(long publicId) {
		this.publicId = publicId;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
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

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}
}
