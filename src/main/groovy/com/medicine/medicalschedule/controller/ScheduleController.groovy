package com.medicine.medicalschedule.controller

import com.medicine.medicalschedule.domain.Schedule
import com.medicine.medicalschedule.service.ScheduleService
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
@RequestMapping("schedule")
class ScheduleController {

    @Autowired
    ScheduleService scheduleService

    @GetMapping
    List<Schedule> getAllSchedules() {
        scheduleService.getAllSchedules()
    }

    @GetMapping("/{id}")
    Schedule getSchedulesById(@PathVariable("id") String id) {
        scheduleService.getScheduleById(id)
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSchedulesById(@PathVariable("id") String id) {
        scheduleService.deleteSchedule(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Schedule createSchedule(@RequestBody Schedule schedule) {
        scheduleService.createSchedule(schedule)
    }
}
