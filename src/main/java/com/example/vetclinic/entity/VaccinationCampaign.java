package com.example.vetclinic.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class VaccinationCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vaccineName;
    private LocalDateTime date;
    private int availableSlots;

    // Getters
    public Long getId() {
        return id;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = availableSlots;
    }
}
