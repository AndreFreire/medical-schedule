package com.medicine.medicalschedule.service

import com.medicine.medicalschedule.domain.Appointment
import com.medicine.medicalschedule.domain.Timetable
import com.medicine.medicalschedule.exception.InvalidException
import com.medicine.medicalschedule.exception.NotFoundException
import com.medicine.medicalschedule.repository.AppointmentRepository
import spock.lang.Specification

import java.time.LocalDateTime

class AppointmentServiceTest extends Specification {
    AppointmentRepository appointmentRepositoryMock
    TimetableService timetableServiceMock
    AppointmentService appointmentService

    void setup() {
        appointmentRepositoryMock = Mock(AppointmentRepository)
        timetableServiceMock = Mock(TimetableService)
        appointmentService = new AppointmentService(appointmentRepositoryMock, timetableServiceMock)
    }

    def "should find all appointments"() {
        given:
            def appointment1 = new Appointment(
                    doctorName: "dr. doctor",
                    doctorId: "123",
                    appointmentDate: LocalDateTime.now(),
                    patientName: "Patient",
                    patientId: "321"
            )
            def appointment2 = new Appointment(
                    doctorName: "dr. doctor",
                    doctorId: "123",
                    appointmentDate: LocalDateTime.now().plusMinutes(30),
                    patientName: "Patient2",
                    patientId: "231"
            )
        when:
            def appointments = appointmentService.getAllAppointments()
        then:
            1 * appointmentRepositoryMock.findAll() >> [appointment1, appointment2]
            "dr. doctor" == appointments[0].doctorName
            "123" == appointments[0].doctorId
            "Patient" == appointments[0].patientName
            "321" == appointments[0].patientId
            "dr. doctor" == appointments[1].doctorName
            "123" == appointments[1].doctorId
            "Patient2" == appointments[1].patientName
            "231" == appointments[1].patientId
    }

    def "should return empty list in find all appointments"() {
        when:
            def appointments = appointmentService.getAllAppointments()
        then:
            1 * appointmentRepositoryMock.findAll() >> []
            appointments.isEmpty()
    }

    def "should find by id appointments"() {
        given:
            def appointment = new Appointment(
                    id: UUID.randomUUID().toString(),
                    doctorName: "dr. doctor",
                    doctorId: "123",
                    appointmentDate: LocalDateTime.now(),
                    patientName: "Patient",
                    patientId: "321"
            )

        when:
            def result = appointmentService.getAppointmentById(appointment.id)
        then:
            1 * appointmentRepositoryMock.findById(appointment.id) >> Optional.of(appointment)
            "dr. doctor" == result.doctorName
            "123" == result.doctorId
            "Patient" == result.patientName
            "321" == result.patientId
    }

    def "should not find appointment by id"() {
        given:
            def invalidId =  "1"
        when:
            appointmentService.getAppointmentById(invalidId)
        then:
            1 * appointmentRepositoryMock.findById(invalidId) >> Optional.empty()
            thrown(NotFoundException)
    }

    def "should delete appointment by id"() {
        given:
            def id =  "1"
        when:
            appointmentService.deleteAllAppointments(id)
        then:
            1 * appointmentRepositoryMock.deleteById(id)
            noExceptionThrown()
    }

    def "should create appointment"() {
        given:
            def doctorId = "123"
            def appointment = new Appointment(
                    doctorName: "dr. doctor",
                    doctorId: doctorId,
                    appointmentDate: LocalDateTime.now(),
                    patientName: "Patient",
                    patientId: "321"
            )
            def timetable = new Timetable(
                    doctorName: "dr. doctor",
                    doctorId: doctorId,
                    startAt: appointment.appointmentDate
            )
        when:
            def savedAppointment = appointmentService.createAppointment(appointment)
        then:
            1 * appointmentRepositoryMock.save(appointment) >> appointment
            1 * timetableServiceMock.getDoctorTimetable(doctorId) >> [timetable]
            "dr. doctor" == savedAppointment.doctorName
            "123" == savedAppointment.doctorId
            "Patient" == savedAppointment.patientName
            "321" == savedAppointment.patientId
    }

    def "should not create appointment"() {
        given:
            def doctorId = "123"
            def appointment = new Appointment(
                    doctorName: "dr. doctor",
                    doctorId: doctorId,
                    appointmentDate: LocalDateTime.now(),
                    patientName: "Patient",
                    patientId: "321"
            )

        when:
            appointmentService.createAppointment(appointment)
        then:
            0 * appointmentRepositoryMock.save(appointment)
            1 * timetableServiceMock.getDoctorTimetable(doctorId) >> []
            thrown(InvalidException)
    }
}
