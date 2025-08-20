package com.voterverification.application.principal;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.voterverification.application.Entity.Users;

public class UserPrincipal implements UserDetails {
	
	private final Users user;
	
	
	
	public UserPrincipal (Users user) {
		this.user= user;
	}
	
	public Users getUser() {
		return this.user;
	}
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		System.out.println(user.getRole().getValue());
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+user.getRole().getValue()));
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}
	
//	@Override
//    public boolean isAccountNonLocked() {
//        // Check if the user is active or not based on the isUserActive field
//        return user.isUserActive();
//    }
//	
//	@Override
//    public boolean isEnabled() {
//        return user.isUserActive();  // You can use the same logic as account non-locked
//    }
// 
}
