package com.example.vetclinic.entity;

import jakarta.persistence.*;

@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String species;

    @ManyToOne
    private User owner;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public User getOwner() { return owner; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSpecies(String species) { this.species = species; }
    public void setOwner(User owner) { this.owner = owner; }
}
