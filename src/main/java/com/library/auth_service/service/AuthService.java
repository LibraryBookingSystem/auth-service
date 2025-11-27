package com.library.auth_service.service;

import com.library.auth_service.dto.*;
import com.library.auth_service.exception.AuthenticationException;
import com.library.auth_service.exception.UserServiceException;
import com.library.auth_service.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service for handling authentication operations
 * Communicates with user-service for user operations
 */
@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    
    @Value("${user-service.url}")
    private String userServiceUrl;
    
    public AuthService(RestTemplate restTemplate, JwtUtil jwtUtil) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * Register a new user
     */
    public AuthResponse register(RegisterRequest request) {
        logger.info("Attempting to register user: {}", request.getUsername());
        
        try {
            // Create user in user-service
            CreateUserRequest createUserRequest = new CreateUserRequest(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CreateUserRequest> entity = new HttpEntity<>(createUserRequest, headers);
            
            ResponseEntity<UserResponse> response = restTemplate.exchange(
                userServiceUrl + "/api/users/internal/create",
                HttpMethod.POST,
                entity,
                UserResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                UserResponse user = response.getBody();
                
                // Generate JWT token
                String token = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getRole(),
                    user.getId()
                );
                
                logger.info("User registered successfully: {}", user.getUsername());
                return new AuthResponse(token, user);
            } else {
                throw new UserServiceException("Failed to create user");
            }
            
        } catch (HttpClientErrorException e) {
            logger.error("Error creating user: {}", e.getMessage());
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new AuthenticationException("Username or email already exists");
            }
            throw new UserServiceException("Failed to communicate with user service: " + e.getMessage());
        } catch (RestClientException e) {
            logger.error("Error communicating with user service: ", e);
            throw new UserServiceException("User service is unavailable");
        }
    }
    
    /**
     * Login user
     */
    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for user: {}", request.getUsername());
        
        try {
            // Validate credentials with user-service
            ValidateCredentialsRequest validateRequest = new ValidateCredentialsRequest(
                request.getUsername(),
                request.getPassword()
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ValidateCredentialsRequest> entity = new HttpEntity<>(validateRequest, headers);
            
            ResponseEntity<UserResponse> response = restTemplate.exchange(
                userServiceUrl + "/api/users/internal/validate",
                HttpMethod.POST,
                entity,
                UserResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                UserResponse user = response.getBody();
                
                // Generate JWT token
                String token = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getRole(),
                    user.getId()
                );
                
                logger.info("User logged in successfully: {}", user.getUsername());
                return new AuthResponse(token, user);
            } else {
                throw new AuthenticationException("Invalid username or password");
            }
            
        } catch (HttpClientErrorException e) {
            logger.error("Authentication failed: {}", e.getMessage());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationException("Invalid username or password");
            }
            throw new UserServiceException("Failed to communicate with user service");
        } catch (RestClientException e) {
            logger.error("Error communicating with user service: ", e);
            throw new UserServiceException("User service is unavailable");
        }
    }
}
