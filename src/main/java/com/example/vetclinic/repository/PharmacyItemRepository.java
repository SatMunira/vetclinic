package com.example.vetclinic.repository;

import com.example.vetclinic.entity.PharmacyItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacyItemRepository extends JpaRepository<PharmacyItem, Long> {
}
