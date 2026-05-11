package io.github.bucheapp.roost.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private long id;
	
	@Column(length = 20)
	@NotBlank
	@Size(min = 3, max = 20)
	@Pattern(regexp = "^[\\p{L}\\p{N}?!_・\\(\\)]+$")
	private String name;
	
	@Column(unique = true)
	private long publicId;
	
	@Column(length = 255)
	@Email
	@Size(max = 255)
	@Pattern(regexp = ".*\\S.*")
	private String email;
	
	@Column
	@NotBlank
	private String password;
	
	@Column
	@Enumerated(EnumType.STRING)
	@NotNull
	UserState state;
	
	@ManyToOne
	@JoinColumn(name = "role_id")
	@NotNull
	private Role role;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "user_permissions",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "permission_id")
	)
	private Set<Permission> permissions;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Profile profile;
	
	public User() {
		this.permissions = new HashSet<>();
	}
	
	public User(
			long publicId,
			String name,
			String email,
			String password,
			UserState state,
			Profile profile,
			Role role
			) {
		this.publicId = publicId;
		this.name = name;
		this.password = password;
		this.state = state;
		this.profile = profile;
		this.role = role;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPublicId() {
		return publicId;
	}

	public void setPublicId(long publicId) {
		this.publicId = publicId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public UserState getState() {
		return state;
	}

	public void setState(UserState state) {
		this.state = state;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
}
