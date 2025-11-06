package com.example.demo.dto;

public class JwtResponse {
    private String token;
    private String id;
    private String name;
    private String email;
    private String role;
    
    public JwtResponse() {}
    
    public JwtResponse(String token, String id, String name, String email, String role) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
    
    public static JwtResponseBuilder builder() {
        return new JwtResponseBuilder();
    }
    
    public static class JwtResponseBuilder {
        private String token;
        private String id;
        private String name;
        private String email;
        private String role;
        
        public JwtResponseBuilder token(String token) { this.token = token; return this; }
        public JwtResponseBuilder id(String id) { this.id = id; return this; }
        public JwtResponseBuilder name(String name) { this.name = name; return this; }
        public JwtResponseBuilder email(String email) { this.email = email; return this; }
        public JwtResponseBuilder role(String role) { this.role = role; return this; }
        
        public JwtResponse build() {
            return new JwtResponse(token, id, name, email, role);
        }
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}