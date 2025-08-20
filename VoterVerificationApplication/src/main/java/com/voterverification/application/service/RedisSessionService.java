//package com.voterverification.application.service;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.dao.DataAccessException;
//import org.springframework.data.redis.RedisConnectionFailureException;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.transaction.Transactional;
//
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class RedisSessionService {
//
//    private final StringRedisTemplate redisTemplate;
//    
//    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSessionService.class);
//
//    public RedisSessionService(StringRedisTemplate redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    	
//    }
//    
//    @PostConstruct
//    public void isRedisConnected() {
//    	try {
//    	String ping = redisTemplate.getConnectionFactory().getConnection().ping();
//    	LOGGER.info("Redis is connected and its saying: "+ping);
//    	}
//    	catch(RedisConnectionFailureException e) {
//    		throw new RuntimeException("Please connect to redis server: "+e);
//    	}
//    	catch ( DataAccessException  e) {
//    		throw new RuntimeException("Please connect to redis server: "+e);
//		}
//    	
//    }
//
//    // Store JWT token in Redis with expiration time
//    @Transactional
//    public void storeUserSession(String username, String jwtToken, long expirationTimeInSeconds) {
//        String key = "SESSION:" + username;
//        redisTemplate.opsForValue().set(key, jwtToken, expirationTimeInSeconds, TimeUnit.SECONDS);
//    }
//
//    // Check if user is already logged in
//    @Transactional
//    public boolean isUserLoggedIn(String username) {
//        return redisTemplate.hasKey("SESSION:" + username);
//    }
//
//    // Remove user session (on logout)
//    @Transactional
//    public void removeUserSession(String username) {
//        redisTemplate.delete("SESSION:" + username);
//    }
//    
//    @Transactional
//    public boolean isTokenBlacklisted(String token) {
//        return redisTemplate.hasKey("BLACKLIST:" + token);
//    }
// 
//    @Transactional
//    public void blacklistToken(String token, long expirationTime) {
//        redisTemplate.opsForValue().set("BLACKLIST:" + token, "true", expirationTime, TimeUnit.SECONDS); // Expiry time should match JWT expiration
//    }
//}
