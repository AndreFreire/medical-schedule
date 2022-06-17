package com.medicine.medicalschedule.service

import com.medicine.medicalschedule.domain.Schedule
import com.medicine.medicalschedule.domain.Timetable
import com.medicine.medicalschedule.repository.AppointmentRepository
import com.medicine.medicalschedule.repository.ScheduleRepository
import org.springframework.stereotype.Service

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class TimetableService {

    private final AppointmentRepository appointmentRepository
    private final ScheduleRepository scheduleRepository

    TimetableService(AppointmentRepository appointmentRepository, ScheduleRepository scheduleRepository) {
        this.appointmentRepository = appointmentRepository
        this.scheduleRepository = scheduleRepository
    }

    List<Timetable> getDoctorTimetable(
            String doctorId,
            LocalDate startDate = LocalDate.now(),
            LocalDate endDate = LocalDate.now().plusWeeks(1)
    ) {

        def schedules = scheduleRepository.findByDoctorId(doctorId)
        def appointments = appointmentRepository.findByDoctorIdAndAppointmentDateBetween(
                doctorId, startDate, endDate
        ).collect {it.appointmentDate}
        def timetables = getAllTimetable(schedules, startDate, endDate)

        timetables.stream()
                .filter{ !appointments.contains(it.startAt)}
                .toList()
    }

    private List<Timetable> getAllTimetable(List<Schedule> schedules, LocalDate startDate, LocalDate endDate) {

        startDate.datesUntil(endDate).map {
            schedules.stream()
                    .filter { sched -> sched.dayOfWeek == it.dayOfWeek }
                    .map { sched ->
                        int slots = (sched.startAt.until(sched.finishAt, ChronoUnit.MINUTES) / sched.slotTime) - 1
                        (0..slots).stream().map { slot ->
                                    new Timetable(
                                            doctorId: sched.doctorId,
                                            doctorName: sched.doctorName,
                                            startAt: LocalDateTime.of(
                                                    it,
                                                    sched.startAt.plusMinutes(sched.slotTime * slot)
                                            )
                                    )
                        }.toList()
                    }.toList().flatten()
        }.toList().flatten() as List<Timetable>
    }
}
