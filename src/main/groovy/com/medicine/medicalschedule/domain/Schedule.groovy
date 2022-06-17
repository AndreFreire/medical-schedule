package com.medicine.medicalschedule.domain

import org.springframework.data.annotation.Id

import java.time.DayOfWeek
import java.time.LocalTime

class Schedule {
    @Id
    String id
    String doctorName
    String doctorId
    LocalTime startAt
    LocalTime finishAt
    DayOfWeek dayOfWeek
    Integer slotTime
}
