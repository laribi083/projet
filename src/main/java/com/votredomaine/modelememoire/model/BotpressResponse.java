// BotpressResponse.java
package com.votredomaine.modelememoire.model;

import java.util.HashMap;
import java.util.Map;

public class BotpressResponse {
    
    private String text;
    private String type; // "text", "quick_reply", "carousel"
    private Map<String, Object> data;
    private boolean shouldContinue;
    
    // Constructeurs
    public BotpressResponse() {
        this.data = new HashMap<>();
        this.shouldContinue = false;
    }
    
    public BotpressResponse(String text) {
        this();
        this.text = text;
        this.type = "text";
    }
    
    // Getters
    public String getText() { return text; }
    public String getType() { return type; }
    public Map<String, Object> getData() { return data; }
    public boolean isShouldContinue() { return shouldContinue; }
    
    // Setters
    public void setText(String text) { this.text = text; }
    public void setType(String type) { this.type = type; }
    public void setData(Map<String, Object> data) { this.data = data; }
    public void setShouldContinue(boolean shouldContinue) { this.shouldContinue = shouldContinue; }
    
    // Méthodes utilitaires
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }
    
    public static BotpressResponse success(String text) {
        return new BotpressResponse(text);
    }
    
    public static BotpressResponse error(String errorMessage) {
        BotpressResponse response = new BotpressResponse();
        response.setText("❌ " + errorMessage);
        response.setType("text");
        return response;
    }
}