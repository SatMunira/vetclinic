package com.example.vetclinic.repository;

import com.example.vetclinic.entity.MedicalRecord;
import com.example.vetclinic.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPet(Pet pet);
}
