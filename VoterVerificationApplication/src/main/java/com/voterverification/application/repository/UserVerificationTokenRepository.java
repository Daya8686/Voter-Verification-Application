package com.voterverification.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.voterverification.application.Entity.UserVerificationToken;
import com.voterverification.application.Entity.Users;

import jakarta.transaction.Transactional;

@Repository
public interface UserVerificationTokenRepository extends JpaRepository<UserVerificationToken, Long>{

	public UserVerificationToken findByUser(Users checkUser);
	
	public Optional<UserVerificationToken> findByToken(String token);
	
	
	@Modifying
	@Query(value = "DELETE FROM user_verification_token WHERE expiration_time < NOW()", nativeQuery = true)
	public void deleteExpiredVerificationTokens();

	public void deleteByToken(String token);

}
