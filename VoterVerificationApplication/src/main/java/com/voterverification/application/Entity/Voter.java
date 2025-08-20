package com.voterverification.application.Entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "voters_info")
public class Voter {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "voter_id")
	private UUID voterId;

	@Column(name = "serial_number", nullable = false, unique = true)
	private Integer serialNumber; // S.No from Excel

	@Column(name = "voter_fullname", nullable = false)
	private String fullName;

	@Column(name = "father_name", nullable = false)
	private String fatherName;

	@Column(name = "voter_age")
	private Integer voterAge;

	@Column(name = "house_no")
	private String houseNo;

	@Column(name = "street")
	private String street;

	@Column(name = "ward")
	private String ward;

	@Column(name = "village")
	private String village;

	@Column(unique = true, nullable = false)
	private String uniqueCode; // Generated at Spring Batch process

	@Column(name = "fingerprint_id", columnDefinition = "BLOB")
    private byte[] fingerprintTemplate; // Stores fingerprint template for matching

	@Column(name = "is_voted")
	private boolean isVoted; // Default false
	
	@Column(name="is_verified")
	private boolean isVerified;
	
	@Column(name="verified_time")
	private LocalDateTime verifiedTime;
	
	@ManyToOne
	@JoinColumn(name = "verified_by")
	private Users verifiedBy;

	@ManyToOne
	@JoinColumn(name = "election_board_id", nullable = false)
	private ElectionBoard electionBoard; // Voter belongs to a board

}
