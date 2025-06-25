package com.example.vetclinic.repository;

import com.example.vetclinic.entity.DoctorSchedule;
import com.example.vetclinic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.doctor = :doctor AND LOWER(ds.dayOfWeek) = :dayOfWeek")
    List<DoctorSchedule> findByDoctorAndDayOfWeekIgnoreCase(@Param("doctor") User doctor, @Param("dayOfWeek") String dayOfWeek);

}
