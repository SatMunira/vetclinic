package com.example.vetclinic.controller;

import com.example.vetclinic.entity.Role;
import com.example.vetclinic.entity.User;
import com.example.vetclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/vets")
    public List<User> getVets() {
        return userRepository.findByRole(Role.VET);
    }
}
