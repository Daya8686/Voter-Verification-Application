package com.voterverification.application.DTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class VoterTableToCSV {

	private Integer serialNumber; // S.No from Excel

	private String fullName;

	private String fatherName;

	private Integer voterAge;

	private boolean isVoted; // Default false

	private String verifiedTime;

	private String houseNo;

	private String street;

	private String ward;

	private String village;

	private String verifiedBy;
	
	 

}
