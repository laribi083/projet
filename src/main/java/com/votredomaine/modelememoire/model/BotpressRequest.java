// BotpressRequest.java
package com.votredomaine.modelememoire.model;

import java.util.Map;

public class BotpressRequest {
    
    private Long userId;
    private String message;
    private String intent;
    private String sessionId;
    private Map<String, Object> payload;
    private double confidence;
    
    // Constructeurs
    public BotpressRequest() {}
    
    public BotpressRequest(Long userId, String message, String intent) {
        this.userId = userId;
        this.message = message;
        this.intent = intent;
    }
    
    // Getters
    public Long getUserId() { return userId; }
    public String getMessage() { return message; }
    public String getIntent() { return intent; }
    public String getSessionId() { return sessionId; }
    public Map<String, Object> getPayload() { return payload; }
    public double getConfidence() { return confidence; }
    
    // Setters
    public void setUserId(Long userId) { this.userId = userId; }
    public void setMessage(String message) { this.message = message; }
    public void setIntent(String intent) { this.intent = intent; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    @Override
    public String toString() {
        return "BotpressRequest{userId=" + userId + ", message='" + message + "', intent='" + intent + "'}";
    }
}