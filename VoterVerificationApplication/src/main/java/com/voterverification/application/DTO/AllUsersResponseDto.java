package com.voterverification.application.DTO;

import java.util.UUID;

import com.voterverification.application.Entity.ElectionBoard;
import com.voterverification.application.Entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllUsersResponseDto {

	private UUID id;

	private String username;

	private String firstName;

	private String lastName;

	private Role role;

	private boolean isUserActive;

	private boolean isUserValid;

	private ContactDto contactDto;

	private ElectionBoardDto electionBoardDto;

	private boolean termsAndConditions;

	private String gender;

}
