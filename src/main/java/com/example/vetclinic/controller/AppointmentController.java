package com.example.vetclinic.controller;

import com.example.vetclinic.dto.AppointmentRequest;
import com.example.vetclinic.entity.Appointment;
import com.example.vetclinic.entity.Pet;
import com.example.vetclinic.entity.Role;
import com.example.vetclinic.entity.User;
import com.example.vetclinic.repository.AppointmentRepository;
import com.example.vetclinic.repository.PetRepository;
import com.example.vetclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.example.vetclinic.entity.Role;


import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest req, Principal principal) {
        User client = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Client not found"));

        Pet pet = petRepository.findById(req.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        User vet = userRepository.findById(req.getVetId())
                .orElseThrow(() -> new RuntimeException("Vet not found"));

        Appointment appointment = new Appointment();
        appointment.setPet(pet);
        appointment.setVet(vet);
        appointment.setClient(client);
        appointment.setReason(req.getReason());
        appointment.setAppointmentTime(req.getTime());

        return ResponseEntity.ok(appointmentRepository.save(appointment));
    }


    @GetMapping
    public ResponseEntity<?> getMyAppointments(Principal principal) {
        User client = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Client not found"));

        List<Appointment> list = appointmentRepository.findByClient(client);
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id,
                                               @RequestParam(required = false) String time,
                                               @RequestParam(required = false) String reason,
                                               Principal principal) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // только владелец записи может изменить
        if (!appointment.getClient().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).body("You are not allowed to edit this appointment");
        }

        if (time != null) {
            appointment.setAppointmentTime(LocalDateTime.parse(time));
        }
        if (reason != null) {
            appointment.setReason(reason);
        }

        return ResponseEntity.ok(appointmentRepository.save(appointment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointmentRepository.delete(appointment);
        return ResponseEntity.ok("Deleted");
    }


    @GetMapping("/vet")
    public ResponseEntity<?> getVetAppointments(Principal principal) {
        User vet = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Vet not found"));

        if (vet.getRole() != Role.VET) {
            return ResponseEntity.status(403).body("Only veterinarians can access this");
        }

        List<Appointment> appointments = appointmentRepository.findByVetAndCompletedFalse(vet);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/client/active")
    public ResponseEntity<?> getActiveAppointments(Principal principal) {
        User client = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Client not found"));

        List<Appointment> list = appointmentRepository.findByClient(client).stream()
                .filter(a -> !Boolean.TRUE.equals(a.isCompleted()))
                .toList();

        return ResponseEntity.ok(list);
    }


}
