package com.voterverification.application.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.voterverification.application.Entity.Users;
import com.voterverification.application.principal.UserPrincipal;

public class UserPrincipalObject {
	
	public static Users getUser() {
		UserDetails userDetails =(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Users users = ((UserPrincipal)userDetails).getUser();
		return users;
	}

}
