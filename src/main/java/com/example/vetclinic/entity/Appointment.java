package com.example.vetclinic.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime appointmentTime;

    private String reason;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "vet_id")
    private User vet;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @Column(nullable = false)
    private boolean completed = false;


    // --- Getters and Setters ---
    public Long getId() { return id; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public String getReason() { return reason; }
    public Pet getPet() { return pet; }
    public User getVet() { return vet; }
    public User getClient() { return client; }

    public void setId(Long id) { this.id = id; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public void setReason(String reason) { this.reason = reason; }
    public void setPet(Pet pet) { this.pet = pet; }
    public void setVet(User vet) { this.vet = vet; }
    public void setClient(User client) { this.client = client; }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
