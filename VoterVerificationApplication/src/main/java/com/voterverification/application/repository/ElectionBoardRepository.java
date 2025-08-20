package com.voterverification.application.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voterverification.application.Entity.ElectionBoard;

@Repository
public interface ElectionBoardRepository extends JpaRepository<ElectionBoard, UUID> {
	
	public ElectionBoard findByElectionBoardCode(String electionBoardcode);

	public boolean existsByElectionBoardCode(String code);

}
