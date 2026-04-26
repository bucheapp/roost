package io.github.bucheapp.roost.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "members")
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id")
	@NotNull
	Community community;
	
	@Column
	@NotNull
	LocalDateTime time;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	User user;
	
	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	MemberState state;
	
	public Member() {}
	
	public Member(
			MemberState state,
			LocalDateTime time,
			User user,
			Community community
			) {
		this.state = state;
		this.time = time;
		this.user = user;
		this.community = community;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Member)) return false;
		Member other = (Member) o;
		return id != 0 && id == other.id;
	}

	public MemberState getState() {
		return state;
	}

	public void setState(MemberState state) {
		this.state = state;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}
}
