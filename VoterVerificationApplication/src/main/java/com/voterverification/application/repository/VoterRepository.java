package com.voterverification.application.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voterverification.application.Entity.Users;
import com.voterverification.application.Entity.Voter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoterRepository extends JpaRepository<Voter, UUID> {
    Optional<Voter> findBySerialNumber(Integer serialNumber);
    List<Voter> findByIsVotedTrue(); // Fetch all voters who have already voted
	List<Voter> findByVerifiedBy(Users user);
}
