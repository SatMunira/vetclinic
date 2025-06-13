package com.example.vetclinic.dto;

import java.time.LocalDateTime;

public class AppointmentRequest {
    private Long petId;
    private Long vetId;
    private String reason;
    private LocalDateTime time;

    // getters and setters
    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public Long getVetId() { return vetId; }
    public void setVetId(Long vetId) { this.vetId = vetId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
}
