package com.voterverification.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.voterverification.application.Entity.ElectionBoard;
import com.voterverification.application.Entity.Users;

@Repository
public interface UsersRepoistory extends JpaRepository<Users, UUID>{
	
	public Users findByUsername(String username);
	
	 @Query("SELECT u FROM Users u WHERE u.contact.email = :email")
	    Optional<Users> findByEmail(@Param("email") String email);

	public List<Users> findByElectionBoard(ElectionBoard electionBoard);

}
