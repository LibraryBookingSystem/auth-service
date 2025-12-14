package com.library.auth_service.dto;

/**
 * Response DTO for token validation
 */
public class TokenValidationResponse {
    private boolean valid;
    private String username;
    private String role;
    private Long userId;
    private String message;
    
    public TokenValidationResponse() {}
    
    public TokenValidationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
    
    public TokenValidationResponse(boolean valid, String username, String role, Long userId) {
        this.valid = valid;
        this.username = username;
        this.role = role;
        this.userId = userId;
        this.message = "Token is valid";
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}

