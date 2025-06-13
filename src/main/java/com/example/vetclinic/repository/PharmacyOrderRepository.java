package com.example.vetclinic.repository;

import com.example.vetclinic.entity.PharmacyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacyOrderRepository extends JpaRepository<PharmacyOrder, Long> {
}
