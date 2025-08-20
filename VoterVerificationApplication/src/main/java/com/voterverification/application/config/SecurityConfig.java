package com.voterverification.application.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.voterverification.application.filter.JWTFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig  {
	
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private JWTFilter jwtFilter;
	
	@Autowired
    private AccessDeniedHandler accessDeniedHandler; 
	
	@Bean
	public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.csrf(customizer->customizer.disable())
					.authorizeHttpRequests(request->request.requestMatchers("/auth/register/**", "/auth/login","/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**","/auth/reset-password/**","/auth/verification/**","/auth/forgot-password/**").permitAll()
			                .requestMatchers("/api/**","/auth/change-password/**", "/auth/update-user-info/**","/auth/user-info/**","/auth/users/**","/auth/logout/**") 
//			                .anyRequest()
			                .authenticated()
			                
			                
			            )
						.exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler)) // Add the custom handler
					
			            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
		
		
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		authenticationProvider.setUserDetailsService(userDetailsService);
		return authenticationProvider;
	}
	
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

	 @Bean
	    public BCryptPasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }


}
