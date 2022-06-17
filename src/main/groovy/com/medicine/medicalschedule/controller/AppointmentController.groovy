package com.medicine.medicalschedule.controller

import com.medicine.medicalschedule.domain.Appointment
import com.medicine.medicalschedule.service.AppointmentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("appointment")
class AppointmentController {

    @Autowired
    AppointmentService appointmentService

    @GetMapping
    List<Appointment> getAllAppointments() {
        appointmentService.getAllAppointments()
    }

    @GetMapping("/{id}")
    Appointment getAppointmentsById(@PathVariable("id") String id) {
        appointmentService.getAppointmentById(id)
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAppointmentsById(@PathVariable("id") String id) {
        appointmentService.deleteAllAppointments(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Appointment createAppointment(@RequestBody Appointment appointment) {
        appointmentService.createAppointment(appointment)
    }

}
