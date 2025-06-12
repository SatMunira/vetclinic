package com.example.vetclinic.repository;

import com.example.vetclinic.entity.VaccinationCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccinationCampaignRepository extends JpaRepository<VaccinationCampaign, Long> {}