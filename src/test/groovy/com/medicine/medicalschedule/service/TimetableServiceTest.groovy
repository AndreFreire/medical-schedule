package com.medicine.medicalschedule.service

import com.medicine.medicalschedule.domain.Appointment
import com.medicine.medicalschedule.domain.Schedule
import com.medicine.medicalschedule.repository.AppointmentRepository
import com.medicine.medicalschedule.repository.ScheduleRepository
import spock.lang.Specification

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

class TimetableServiceTest extends Specification {
    AppointmentRepository appointmentRepositoryMock
    ScheduleRepository scheduleRepositoryMock
    TimetableService timetableService

    void setup() {
        appointmentRepositoryMock = Mock(AppointmentRepository)
        scheduleRepositoryMock = Mock(ScheduleRepository)
        timetableService = new TimetableService(appointmentRepositoryMock, scheduleRepositoryMock)
    }

    def "should get timetable"() {
        given:
            def doctorId = "123"
            def dayOfWeek = DayOfWeek.WEDNESDAY
            def nextDay = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek))
            def schedule = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: doctorId,
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: dayOfWeek,
                    slotTime: 30
            )
        when:
            def timetable = timetableService.getDoctorTimetable(doctorId)
        then:
            1 * scheduleRepositoryMock.findByDoctorId(doctorId) >> [schedule]
            1 * appointmentRepositoryMock.findByDoctorIdAndAppointmentDateBetween(_, _, _)
            "123" == timetable[0].doctorId
            "dr strange" == timetable[0].doctorName
            LocalDateTime.of(nextDay, LocalTime.of(18,0)) == timetable[0].startAt
            "123" == timetable[1].doctorId
            "dr strange" == timetable[1].doctorName
            LocalDateTime.of(nextDay, LocalTime.of(18,30)) == timetable[1].startAt
    }

    def "should get timetable in different days"() {
        given:
            def doctorId = "123"
            def dayOfWeek = LocalDate.now().dayOfWeek
            def today = LocalDate.now()
            def schedule = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: doctorId,
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: dayOfWeek,
                    slotTime: 30
            )
            def dayOfWeek2 = LocalDate.now().plusDays(2).dayOfWeek
            def nextDay2 = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek2))
            def schedule2 = new Schedule(
                    id: "2",
                    doctorName: "dr strange",
                    doctorId: doctorId,
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: dayOfWeek2,
                    slotTime: 30
            )
        when:
            def timetable = timetableService.getDoctorTimetable(doctorId)
        then:
            1 * scheduleRepositoryMock.findByDoctorId(doctorId) >> [schedule, schedule2]
            1 * appointmentRepositoryMock.findByDoctorIdAndAppointmentDateBetween(_, _, _)

            "123" == timetable[0].doctorId
            "dr strange" == timetable[0].doctorName
            LocalDateTime.of(today, LocalTime.of(18,0)) == timetable[0].startAt
            "123" == timetable[1].doctorId
            "dr strange" == timetable[1].doctorName
            LocalDateTime.of(today, LocalTime.of(18,30)) == timetable[1].startAt

            "123" == timetable[2].doctorId
            "dr strange" == timetable[2].doctorName
            LocalDateTime.of(nextDay2, LocalTime.of(18,0)) == timetable[2].startAt
            "123" == timetable[3].doctorId
            "dr strange" == timetable[3].doctorName
            LocalDateTime.of(nextDay2, LocalTime.of(18,30)) == timetable[3].startAt
    }

    def "should get timetable with appointments"() {
        given:
            def doctorId = "123"
            def dayOfWeek = DayOfWeek.WEDNESDAY
            def nextDay = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek))
            def schedule = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: doctorId,
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: dayOfWeek,
                    slotTime: 30
            )
            def appointment = new Appointment(
                    doctorName: "dr strange",
                    doctorId: doctorId,
                    appointmentDate: LocalDateTime.of(nextDay, LocalTime.of(18,0)),
                    patientName: "Patient",
                    patientId: "321"
            )
        when:
            def timetable = timetableService.getDoctorTimetable(doctorId)
        then:
            1 * scheduleRepositoryMock.findByDoctorId(doctorId) >> [schedule]
            1 * appointmentRepositoryMock.findByDoctorIdAndAppointmentDateBetween(_, _, _) >> [appointment]
            "123" == timetable[0].doctorId
            "dr strange" == timetable[0].doctorName
            LocalDateTime.of(nextDay, LocalTime.of(18,30)) == timetable[0].startAt
    }

    def "should get empty timetable"() {
        given:
            def doctorId = "123"
            def dayOfWeek = DayOfWeek.WEDNESDAY
            def nextDay = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek))
            def schedule = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: doctorId,
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(18,30),
                    dayOfWeek: dayOfWeek,
                    slotTime: 30
            )
            def appointment = new Appointment(
                    doctorName: "dr strange",
                    doctorId: doctorId,
                    appointmentDate: LocalDateTime.of(nextDay, LocalTime.of(18,0)),
                    patientName: "Patient",
                    patientId: "321"
            )
        when:
            def timetable = timetableService.getDoctorTimetable(doctorId)
        then:
            1 * scheduleRepositoryMock.findByDoctorId(doctorId) >> [schedule]
            1 * appointmentRepositoryMock.findByDoctorIdAndAppointmentDateBetween(_, _, _) >> [appointment]
            timetable.isEmpty()
    }

    def "should get empty timetable when doctor doest have schedule"() {
        given:
            def doctorId = "123"
        when:
            def timetable = timetableService.getDoctorTimetable(doctorId)
        then:
            1 * scheduleRepositoryMock.findByDoctorId(doctorId) >> []
            1 * appointmentRepositoryMock.findByDoctorIdAndAppointmentDateBetween(_, _, _) >> []
            timetable.isEmpty()
    }

}
