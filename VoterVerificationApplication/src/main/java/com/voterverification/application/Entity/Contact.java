package com.voterverification.application.Entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "contact_id")
	private UUID contactId;


	@Column(nullable = false, unique = true)
	private String mobile;

	@Column(nullable = false, unique = true)
	private String email;

}
