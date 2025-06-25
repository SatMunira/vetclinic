package com.example.vetclinic.controller;

import com.example.vetclinic.entity.DoctorSchedule;
import com.example.vetclinic.entity.User;
import com.example.vetclinic.repository.AppointmentRepository;
import com.example.vetclinic.repository.DoctorScheduleRepository;
import com.example.vetclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vet")
public class VetScheduleController {

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/{vetId}/schedule")
    public ResponseEntity<?> getAvailableSlots(@PathVariable Long vetId,
                                               @RequestParam String date) {
        // Парсим дату
        LocalDate localDate = LocalDate.parse(date);
        String dayOfWeek = localDate.getDayOfWeek().toString().toLowerCase();


        User doctor = userRepository.findById(vetId).orElse(null);
        if (doctor == null) return ResponseEntity.badRequest().body("Врач не найден");

        // Получаем расписание врача на день недели
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorAndDayOfWeekIgnoreCase(doctor, dayOfWeek);
        if (schedules.isEmpty()) return ResponseEntity.ok(Collections.emptyList());

        // Берём первый график (если их несколько — можно расширить логику)
        DoctorSchedule schedule = schedules.get(0);

        // Все записи врача на дату
        List<LocalTime> takenSlots = appointmentRepository.findByVetAndDate(doctor, localDate).stream()
                .map(a -> a.getAppointmentTime().toLocalTime())
                .collect(Collectors.toList());

        List<String> availableSlots = new ArrayList<>();

        LocalTime slotTime = schedule.getStartTime();
        while (slotTime.plusMinutes(schedule.getSlotDurationMinutes()).compareTo(schedule.getEndTime()) <= 0) {
            final LocalTime currentSlot = slotTime; // Сделать локальную финальную копию для лямбды
            int countTaken = (int) takenSlots.stream().filter(t -> t.equals(currentSlot)).count();

            if (countTaken < schedule.getMaxAppointmentsPerSlot()) {
                availableSlots.add(slotTime.toString());
            }
            slotTime = slotTime.plusMinutes(schedule.getSlotDurationMinutes());
        }


        return ResponseEntity.ok(availableSlots);
    }
}
