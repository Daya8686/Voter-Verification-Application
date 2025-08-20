package com.voterverification.application.DTO;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateElectionBoardDto {

	@NotBlank(message = "Election name cannot be left blank!!")
	@Size(min = 5, max=256, message = "Election name must be min 5 characters and max 256 characters!!")
	private String electionName;
	
	@NotBlank(message = "Election address cannot be left blank!!")
	@Size(min = 2, max=256, message = "Election name must be min 2 characters and max 256 characters!!")
	private String electionAddress;
	
	@NotNull(message = "Election Data cannot be left blank")
	@FutureOrPresent(message = "Election date must be selected which is Present or Future dates only!")
	private LocalDate electionDate;
	
}
