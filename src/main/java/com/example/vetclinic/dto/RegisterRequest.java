package com.example.vetclinic.dto;

import com.example.vetclinic.entity.Role;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private Role role;

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(Role role) { this.role = role; }
}
