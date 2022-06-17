package com.medicine.medicalschedule.repository

import com.medicine.medicalschedule.domain.Appointment
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

import java.time.LocalDate

@Repository
interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByDoctorIdAndAppointmentDateBetween(String doctorId, LocalDate startDate, LocalDate endDate)
}
