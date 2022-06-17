package com.medicine.medicalschedule.service

import com.medicine.medicalschedule.domain.Appointment
import com.medicine.medicalschedule.exception.InvalidException
import com.medicine.medicalschedule.exception.NotFoundException
import com.medicine.medicalschedule.repository.AppointmentRepository
import org.springframework.stereotype.Service

@Service
class AppointmentService {

    private final AppointmentRepository appointmentRepository
    private final TimetableService timetableService

    AppointmentService(AppointmentRepository appointmentRepository, TimetableService timetableService) {
        this.appointmentRepository = appointmentRepository
        this.timetableService = timetableService
    }

    List<Appointment> getAllAppointments() {
        appointmentRepository.findAll()
    }

    Appointment getAppointmentById(String id) {
        appointmentRepository.findById(id)
                .orElseThrow{ new NotFoundException("schedule not found") }
    }

    void deleteAllAppointments(String id) {
        appointmentRepository.deleteById(id)
    }

    Appointment createAppointment(Appointment appointment) {
        validateAppointment(appointment)
        appointmentRepository.save(appointment)
    }

    private void validateAppointment(Appointment appointment) {
        def timetable = timetableService.getDoctorTimetable(appointment.doctorId)
                .collect {it.startAt }

        if (!timetable.contains(appointment.appointmentDate)) {
            throw new InvalidException("schedule not found")
        }
    }
}
