package com.voterverification.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.voterverification.application.Entity.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepostory extends JpaRepository<PasswordResetToken, Long> {
	
	
	@Modifying
	@Query(value = "DELETE FROM password_reset_token where expiration_time < NOW()",nativeQuery = true)
	public void deletePasswordExpiredTokens();
}
