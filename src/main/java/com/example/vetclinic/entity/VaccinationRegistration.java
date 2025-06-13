package com.example.vetclinic.entity;

import jakarta.persistence.*;

@Entity
public class VaccinationRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int animalCount;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private VaccinationCampaign campaign;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public int getAnimalCount() {
        return animalCount;
    }

    public VaccinationCampaign getCampaign() {
        return campaign;
    }

    public User getClient() {
        return client;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setAnimalCount(int animalCount) {
        this.animalCount = animalCount;
    }

    public void setCampaign(VaccinationCampaign campaign) {
        this.campaign = campaign;
    }

    public void setClient(User client) {
        this.client = client;
    }
}
