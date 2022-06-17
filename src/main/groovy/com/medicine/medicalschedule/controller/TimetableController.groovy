package com.medicine.medicalschedule.controller

import com.medicine.medicalschedule.domain.Timetable
import com.medicine.medicalschedule.service.TimetableService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("timetable")
class TimetableController {

    @Autowired
    TimetableService timetableService

    @GetMapping
    List<Timetable> getDoctorTimeTable(@RequestParam(required = true, name="doctorId") String doctorId) {
        timetableService.getDoctorTimetable(doctorId)
    }
}
