package com.medicine.medicalschedule.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import java.time.LocalDateTime

@Document("appointment")
class Appointment {
    @Id
    String id
    String doctorName
    String doctorId
    LocalDateTime appointmentDate
    String patientName
    String patientId
}
