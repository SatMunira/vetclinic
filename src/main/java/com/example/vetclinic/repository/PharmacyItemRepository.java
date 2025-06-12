package com.example.vetclinic.repository;

import com.example.vetclinic.entity.PharmacyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyItemRepository extends JpaRepository<PharmacyItem, Long> {}