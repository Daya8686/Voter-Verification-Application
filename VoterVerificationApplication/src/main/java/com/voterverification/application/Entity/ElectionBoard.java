package com.voterverification.application.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "election_board")
public class ElectionBoard {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "election_board_id")
	private UUID electionBoardId;
	
	@Column(nullable = false)
	private String electionName;
	
	@Column(nullable = false)
	private String electionAddress;
	
	@Column(nullable = false)
	private LocalDate electionDate;
	
	@Column(nullable = false)
	private String electionDay;

	@Column(unique = true, nullable = false, length = 9)
	private String electionBoardCode; // Unique 6-digit alphanumeric code

	@OneToMany(mappedBy = "electionBoard", cascade = CascadeType.ALL)
	private List<Users> verifiers; // Verifiers under this board

	@OneToMany(mappedBy = "electionBoard", cascade = CascadeType.ALL)
	private List<Voter> voters;

}
