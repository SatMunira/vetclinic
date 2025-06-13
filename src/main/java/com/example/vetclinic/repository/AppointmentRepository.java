package com.example.vetclinic.repository;

import com.example.vetclinic.entity.Appointment;
import com.example.vetclinic.entity.Pet;
import com.example.vetclinic.entity.Role;
import com.example.vetclinic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByClient(User client);
    List<Appointment> findByVet(User vet);

    Optional<Appointment> findByPetAndVetAndCompletedFalse(Pet pet, User vet);

    List<Appointment> findByVetAndCompletedFalse(User vet);



}
