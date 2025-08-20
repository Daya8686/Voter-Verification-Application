package com.voterverification.application.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voterverification.application.Entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
	
	public Contact findByMobile(String mobileNumber);
	
	public Contact findByEmail(String email);

}
