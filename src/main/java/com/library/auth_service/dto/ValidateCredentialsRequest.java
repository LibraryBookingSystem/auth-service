package com.library.auth_service.dto;

/**
 * Internal DTO for validating user credentials
 */
public class ValidateCredentialsRequest {
    
    private String username;
    private String password;
    
    public ValidateCredentialsRequest() {}
    
    public ValidateCredentialsRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

