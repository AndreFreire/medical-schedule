package com.medicine.medicalschedule.repository

import com.medicine.medicalschedule.domain.Schedule
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

import java.time.DayOfWeek

@Repository
interface ScheduleRepository extends MongoRepository<Schedule, String> {
    List<Schedule> findByDoctorId(String id)

    List<Schedule> findByDoctorIdAndDayOfWeek(String s, DayOfWeek dayOfWeek)
}
