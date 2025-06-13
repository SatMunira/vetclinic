package com.example.vetclinic.controller;

import com.example.vetclinic.entity.Pet;
import com.example.vetclinic.entity.User;
import com.example.vetclinic.repository.PetRepository;
import com.example.vetclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> addPet(@RequestBody Pet pet, Principal principal) {
        User owner = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        pet.setOwner(owner);
        Pet saved = petRepository.save(pet);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<?> getMyPets(Principal principal) {
        User owner = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Pet> pets = petRepository.findByOwner(owner);
        return ResponseEntity.ok(pets);
    }
}
