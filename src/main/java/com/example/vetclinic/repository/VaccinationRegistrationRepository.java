package com.example.vetclinic.repository;

import com.example.vetclinic.entity.VaccinationRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccinationRegistrationRepository extends JpaRepository<VaccinationRegistration, Long> {
}
