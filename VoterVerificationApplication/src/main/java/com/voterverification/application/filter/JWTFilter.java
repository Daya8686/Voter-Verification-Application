package com.voterverification.application.filter;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voterverification.application.Entity.Users;
import com.voterverification.application.exception.ErrorResponse;
import com.voterverification.application.principal.UserPrincipal;
import com.voterverification.application.repository.UsersRepoistory;
import com.voterverification.application.service.JWTService;
import com.voterverification.application.service.MyUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter  extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
   private ApplicationContext context;
    
//    @Autowired
//    private RedisSessionService  redisSessionService;
    
    @Autowired
    private ObjectMapper objectMapper; 
    
   
    
    public JWTFilter() {
    	System.out.println("This is in filter");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
    	
    	
    	
//    	String contentType = request.getContentType();
//        if (contentType != null) {
//            if (contentType.startsWith("application/json")) {
//                // Handle JSON request
//                logger.info("Received JSON request");
//            } else if (contentType.startsWith("multipart/form-data")) {
//                // Handle multipart/form-data request
//                logger.info("Received multipart/form-data request");
//            } else if (contentType.startsWith("application/xml")) {
//                // Handle XML request
//                logger.info("Received XML request");
//            } else {
//                logger.warn("Unsupported content type: " + contentType);
//            }
//        }
        

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

//        System.out.println("Authorization header: '" + authHeader + "'");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7).strip(); // strip() handles all whitespace
//            System.out.println("Extracted token: '" + token + "'"); // Print the token without "Bearer "
//        }
//        // Extract token and validate "Bearer" scheme
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
        	 
            token = authHeader.substring(7).trim();
            String mytoken=authHeader.substring(7).strip();
            
            
//            if(redisSessionService.isTokenBlacklisted(token)) {
//            	handleException(response,"This token is invalid now!!", HttpStatus.UNAUTHORIZED);
//            	return;
//            }
           
            
            try {
            System.out.println("This is from here "+token);
            System.out.println("This is from here Another "+mytoken);
            username = jwtService.extractUserName(token);
           
       
           
        // Validate token and set the authentication context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        	
            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
            
//            if(!((UserPrincipal)userDetails).getUser().isUserLoggedIn()) {
//            	handleException(response, "You are logged out!!", HttpStatus.FORBIDDEN);
//            }
            if (jwtService.validateToken(token, userDetails)) {
            	
            	System.out.println(jwtService.validateToken(token, userDetails));
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
            }
        }
        } catch (ExpiredJwtException e) {
            // Handle expired token error and send a custom message
            handleException(response, "Token has expired. Please log in again.", HttpStatus.UNAUTHORIZED);
            return; // Prevent further processing of the filter chain
        } catch (Exception e) {
            // Handle other JWT-related exceptions
            handleException(response, "Invalid token. Please log in again.", HttpStatus.UNAUTHORIZED);
            return; // Prevent further processing of the filter chain
        }
        }

        chain.doFilter(request, response);
    }
    
    private void handleException(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Create a custom error response in JSON format
        String jsonResponse = objectMapper.writeValueAsString(
                new ErrorResponse(status.value(),message , LocalDateTime.now(), null)
        );

        response.getWriter().write(jsonResponse);
    }
  
}

