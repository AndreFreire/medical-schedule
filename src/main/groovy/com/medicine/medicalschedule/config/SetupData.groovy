package com.medicine.medicalschedule.config

import com.medicine.medicalschedule.domain.Appointment
import com.medicine.medicalschedule.domain.Schedule
import com.medicine.medicalschedule.repository.AppointmentRepository
import com.medicine.medicalschedule.repository.ScheduleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@Component
class SetupData {
    @Autowired
    AppointmentRepository appointmentRepository
    @Autowired
    ScheduleRepository scheduleRepository

    @PostConstruct
    void setup() {
        def appointmentDate = LocalDate.now()
        def doctorId = "999999999"
        def scheduleDefault = new Schedule(
                doctorName: "dr strange",
                doctorId: doctorId,
                startAt: LocalTime.of(14,0),
                finishAt: LocalTime.of(18,0),
                dayOfWeek: appointmentDate.dayOfWeek,
                slotTime: 30
        )
        scheduleRepository.save(scheduleDefault)

        def appointmentDefault = new Appointment(
                id: "888888888",
                doctorName: "dr. doctor",
                doctorId: doctorId,
                appointmentDate: LocalDateTime.of(appointmentDate, LocalTime.of(14,30)),
                patientName: "Patient",
                patientId: "321"
        )

        appointmentRepository.save(appointmentDefault)
    }
}
