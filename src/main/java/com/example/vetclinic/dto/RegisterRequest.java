package com.example.vetclinic.dto;

import com.example.vetclinic.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private Role role;
}

