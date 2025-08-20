package com.voterverification.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.voterverification.application.Entity.Users;
import com.voterverification.application.principal.UserPrincipal;
import com.voterverification.application.repository.UsersRepoistory;

@Service
public class MyUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UsersRepoistory usersRepo;
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = usersRepo.findByUsername(username);
		if(user!=null) {
			return new UserPrincipal(user);
		}
		throw new UsernameNotFoundException("User not found with username: " + username);
	}

}
