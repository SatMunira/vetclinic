package com.example.vetclinic.controller;

import com.example.vetclinic.entity.*;
import com.example.vetclinic.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vaccination")
public class VaccinationController {

    @Autowired
    private VaccinationCampaignRepository campaignRepository;

    @Autowired
    private VaccinationRegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    // 👮 Админ: создать кампанию
    @PostMapping("/campaign")
    public ResponseEntity<?> createCampaign(@RequestParam String vaccineName,
                                            @RequestParam String date,
                                            @RequestParam int slots,
                                            Principal principal) {
        User admin = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("Only admins can create campaigns");
        }

        VaccinationCampaign campaign = new VaccinationCampaign();
        campaign.setVaccineName(vaccineName);
        campaign.setDate(LocalDateTime.parse(date));
        campaign.setAvailableSlots(slots);

        return ResponseEntity.ok(campaignRepository.save(campaign));
    }

    // 👁 Клиент и все: список всех кампаний
    @GetMapping("/campaigns")
    public ResponseEntity<?> getCampaigns() {
        return ResponseEntity.ok(campaignRepository.findAll());
    }

    // 👤 Клиент: записаться
    @PostMapping("/register")
    public ResponseEntity<?> registerForVaccination(@RequestParam Long campaignId,
                                                    @RequestParam int animalCount,
                                                    Principal principal) {
        User client = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Client not found"));

        VaccinationCampaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        if (campaign.getAvailableSlots() < animalCount) {
            return ResponseEntity.badRequest().body("Not enough slots available");
        }

        campaign.setAvailableSlots(campaign.getAvailableSlots() - animalCount);
        campaignRepository.save(campaign);

        VaccinationRegistration registration = new VaccinationRegistration();
        registration.setClient(client);
        registration.setAnimalCount(animalCount);
        registration.setCampaign(campaign);

        return ResponseEntity.ok(registrationRepository.save(registration));
    }

    // 👤 Клиент: получить свои заявки на вакцинацию
    @GetMapping("/my-registrations")
    public ResponseEntity<?> getMyRegistrations(Principal principal) {
        User client = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Client not found"));

        List<VaccinationRegistration> registrations = registrationRepository.findAll()
                .stream()
                .filter(reg -> reg.getClient().getId().equals(client.getId()))
                .toList();

        return ResponseEntity.ok(registrations);
    }

    @DeleteMapping("/my-registrations/{id}")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long id, Principal principal) {
        User client = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Client not found"));

        VaccinationRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        // Проверка: пользователь должен быть владельцем этой заявки
        if (!registration.getClient().getId().equals(client.getId())) {
            return ResponseEntity.status(403).body("You are not allowed to delete this registration");
        }

        // Возвращаем слоты обратно
        VaccinationCampaign campaign = registration.getCampaign();
        campaign.setAvailableSlots(campaign.getAvailableSlots() + registration.getAnimalCount());
        campaignRepository.save(campaign);

        registrationRepository.delete(registration);
        return ResponseEntity.ok("Registration canceled");
    }

}
