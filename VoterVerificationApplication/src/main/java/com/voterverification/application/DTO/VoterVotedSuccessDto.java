package com.voterverification.application.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoterVotedSuccessDto {
	
	private UUID voterId;

	
	private Integer serialNumber; // S.No from Excel

	
	private String fullName;

	
	private String fatherName;

	
	private Integer voterAge;
	
	
	private boolean isVoted; // Default false
	
	
	private boolean isVerified;
	
	
	private LocalDateTime verifiedTime;
	
	
	private String houseNo;

	
	private String street;

	
	private String ward;

	
	private String village;
	
	
	private AllUsersResponseDto verifiedBy;
	
	private String electionBoardName;
	

}
