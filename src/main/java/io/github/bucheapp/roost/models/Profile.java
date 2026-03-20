package io.github.bucheapp.roost.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.hibernate.validator.constraints.URL;

@Entity
@Table(name = "user_profiles")
public class Profile {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private long id;
	
	@Column
	private String bio;
	
	@Column
	private String iconUrl;
	
	@Column
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	@Column
	private LocalDate dateOfBirth;
	
	@Column
	private String phoneNumber;
	
	@Column
	private String address;
	
	@URL(protocol = "https", host = "github.com")
	private String githubUrl;
	
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
}
