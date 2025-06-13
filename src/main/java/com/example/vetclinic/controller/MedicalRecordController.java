package com.example.vetclinic.controller;

import com.example.vetclinic.dto.RecordRequest;
import com.example.vetclinic.entity.MedicalRecord;
import com.example.vetclinic.entity.Pet;
import com.example.vetclinic.entity.User;
import com.example.vetclinic.entity.Role;
import com.example.vetclinic.repository.MedicalRecordRepository;
import com.example.vetclinic.repository.PetRepository;
import com.example.vetclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.example.vetclinic.entity.Appointment;
import com.example.vetclinic.repository.AppointmentRepository;


import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/records")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordRepository recordRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;


    // 👨‍⚕️ Ветеринар добавляет запись
    @PostMapping
    public ResponseEntity<?> addRecord(@RequestBody RecordRequest recordRequest,
                                       Principal principal) {

        User vet = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Vet not found"));

        if (vet.getRole() != Role.VET) {
            return ResponseEntity.status(403).body("Only vets can add records");
        }

        Pet pet = petRepository.findById(recordRequest.petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        MedicalRecord record = new MedicalRecord();
        record.setPet(pet);
        record.setVet(vet);
        record.setDate(LocalDateTime.now());
        record.setDiagnosis(recordRequest.diagnosis);
        record.setTreatment(recordRequest.treatment);

        MedicalRecord saved = recordRepository.save(record);

        // ✅ Завершить приём
        appointmentRepository.findByPetAndVetAndCompletedFalse(pet, vet)
                .ifPresent(appointment -> {
                    appointment.setCompleted(true);
                    appointmentRepository.save(appointment);
                });

        return ResponseEntity.ok(saved);
    }



    // 👀 История медкарты питомца (для клиента или врача)
    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getPetRecords(@PathVariable Long petId,
                                           Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        // только владелец или врач
        if (pet.getOwner().getId().equals(user.getId()) || user.getRole() == Role.VET) {
            List<MedicalRecord> records = recordRepository.findByPet(pet);
            return ResponseEntity.ok(records);
        } else {
            return ResponseEntity.status(403).body("Access denied");
        }
    }
}
