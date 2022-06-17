package com.medicine.medicalschedule.service

import com.medicine.medicalschedule.domain.Schedule
import com.medicine.medicalschedule.exception.ConflictException
import com.medicine.medicalschedule.exception.InvalidException
import com.medicine.medicalschedule.exception.NotFoundException
import com.medicine.medicalschedule.repository.ScheduleRepository
import org.springframework.stereotype.Service

import java.time.temporal.ChronoUnit

@Service

class ScheduleService {

    private final ScheduleRepository scheduleRepository

    ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository
    }

    List<Schedule> getAllSchedules() {
        scheduleRepository.findAll()
    }

    Schedule getScheduleById(String id) {
        scheduleRepository.findById(id)
                .orElseThrow{ new NotFoundException("schedule not found") }
    }

    void deleteSchedule(String id) {
        scheduleRepository.deleteById(id)
    }

    Schedule createSchedule(Schedule schedule) {
        createValidation(schedule)
        scheduleRepository.save(schedule)
    }

    private void createValidation(Schedule schedule) {
        validateSlotTime(schedule)
        validateConflict(schedule)
    }

    private void validateSlotTime(schedule) {
        long minutes = schedule.startAt.until(schedule.finishAt,  ChronoUnit.MINUTES)
        if (minutes > 0 && minutes % schedule.slotTime != 0) {
            throw new InvalidException("invalid slotTime")
        }
    }

    private void validateConflict(Schedule schedule) {
        def savedSchedules = scheduleRepository.findByDoctorIdAndDayOfWeek(
                schedule.doctorId, schedule.dayOfWeek
        )
        savedSchedules?.forEach{
            if (it.startAt.isBefore(schedule.startAt) && it.finishAt.isAfter(schedule.startAt) ||
                    it.startAt.isBefore(schedule.finishAt) && it.finishAt.isAfter(schedule.finishAt) ||
                    it.startAt.isAfter(schedule.startAt) && it.finishAt.isBefore(schedule.finishAt) ||
                    it.startAt == schedule.startAt || it.finishAt == schedule.finishAt)
                throw new ConflictException("conflict")
        }
    }
}
