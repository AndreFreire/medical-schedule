package com.medicine.medicalschedule.service

import com.medicine.medicalschedule.domain.Schedule
import com.medicine.medicalschedule.exception.ConflictException
import com.medicine.medicalschedule.exception.InvalidException
import com.medicine.medicalschedule.exception.NotFoundException
import com.medicine.medicalschedule.repository.ScheduleRepository
import spock.lang.Specification
import spock.lang.Unroll

import java.time.DayOfWeek
import java.time.LocalTime

class ScheduleServiceTest extends Specification {
    ScheduleService scheduleService
    ScheduleRepository scheduleRepositoryMock

    void setup () {
        scheduleRepositoryMock = Mock(ScheduleRepository)
        scheduleService = new ScheduleService(scheduleRepositoryMock)
    }

    def "should get schedule by id"() {
        given:
            def scheduleMock = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: "123",
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: DayOfWeek.WEDNESDAY,
                    slotTime: 30
            )
        when:
            def schedule = scheduleService.getScheduleById("1")
        then:
            1 * scheduleRepositoryMock.findById("1") >> Optional.of(scheduleMock)
            "1" == schedule.id
            "dr strange" == schedule.doctorName
            "123" == schedule.doctorId
            LocalTime.of(18,0) == schedule.startAt
            LocalTime.of(19,0) == schedule.finishAt
            DayOfWeek.WEDNESDAY == schedule.dayOfWeek
            30 == schedule.slotTime
    }

    def "should not found schedule by id"() {
        when:
            scheduleService.getScheduleById("1")
        then:
            1 * scheduleRepositoryMock.findById("1") >> Optional.empty()
            thrown(NotFoundException)
    }

    def "should list all schedules"() {
        given:
            def scheduleMock = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: "123",
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: DayOfWeek.WEDNESDAY,
                    slotTime: 30
            )
            def scheduleMock2 = new Schedule(
                    id: "2",
                    doctorName: "dr strange2",
                    doctorId: "321",
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: DayOfWeek.WEDNESDAY,
                    slotTime: 30
            )
        when:
            def schedule = scheduleService.getAllSchedules()
        then:
            1 * scheduleRepositoryMock.findAll() >> [scheduleMock, scheduleMock2]
            "1" == schedule[0].id
            "dr strange" == schedule[0].doctorName
            "123" == schedule[0].doctorId
            LocalTime.of(18,0) == schedule[0].startAt
            LocalTime.of(19,0) == schedule[0].finishAt
            DayOfWeek.WEDNESDAY == schedule[0].dayOfWeek
            30 == schedule[0].slotTime

            "2" == schedule[1].id
            "dr strange2" == schedule[1].doctorName
            "321" == schedule[1].doctorId
            LocalTime.of(18,0) == schedule[1].startAt
            LocalTime.of(19,0) == schedule[1].finishAt
            DayOfWeek.WEDNESDAY == schedule[1].dayOfWeek
            30 == schedule[1].slotTime
    }

    def "should return empty list"() {
        when:
            def schedules = scheduleService.getAllSchedules()
        then:
            1 * scheduleRepositoryMock.findAll() >> []
            schedules.isEmpty()
            noExceptionThrown()
    }

    def "should delete schedule"() {
        given:
            def scheduleId = "1"
        when:
            scheduleService.deleteSchedule(scheduleId)
        then:
            1 * scheduleRepositoryMock.deleteById(scheduleId)
            noExceptionThrown()
    }

    def "should create schedule"() {
        given:
            def schedule = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: "123",
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: DayOfWeek.WEDNESDAY,
                    slotTime: 30
            )
        when:
            def savedSchedule = scheduleService.createSchedule(schedule)
        then:
            1 * scheduleRepositoryMock.save(schedule) >> schedule
            "1" == savedSchedule.id
            "dr strange" == savedSchedule.doctorName
            "123" == savedSchedule.doctorId
            LocalTime.of(18,0) == savedSchedule.startAt
            LocalTime.of(19,0) == savedSchedule.finishAt
            DayOfWeek.WEDNESDAY == savedSchedule.dayOfWeek
            30 == savedSchedule.slotTime
    }

    def "should create schedule in different days"() {
        given:
            def schedule = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: "123",
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: DayOfWeek.WEDNESDAY,
                    slotTime: 30
            )
            def schedule2 = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: "123",
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: DayOfWeek.FRIDAY,
                    slotTime: 30
            )
            scheduleService.createSchedule(schedule2)
        when:
            def savedSchedule = scheduleService.createSchedule(schedule)
        then:
            1 * scheduleRepositoryMock.save(schedule) >> schedule
            "1" == savedSchedule.id
            "dr strange" == savedSchedule.doctorName
            "123" == savedSchedule.doctorId
            LocalTime.of(18,0) == savedSchedule.startAt
            LocalTime.of(19,0) == savedSchedule.finishAt
            DayOfWeek.WEDNESDAY == savedSchedule.dayOfWeek
            30 == savedSchedule.slotTime
    }

    @Unroll
    def "should not create when conflict schedules"() {
        given:
            def schedule = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: "123",
                    startAt: startAt,
                    finishAt: finishAt,
                    dayOfWeek: DayOfWeek.WEDNESDAY,
                    slotTime: 30
            )
            def scheduleSaved = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: "123",
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: DayOfWeek.WEDNESDAY,
                    slotTime: 30
            )
            scheduleService.createSchedule(scheduleSaved)
        when:
            scheduleService.createSchedule(schedule)
        then:
            1 * scheduleRepositoryMock.findByDoctorIdAndDayOfWeek(schedule.doctorId, schedule.dayOfWeek) >> [schedule]
            0 * scheduleRepositoryMock.save(schedule)
            thrown(ConflictException)
        where:
            startAt                          | finishAt
            LocalTime.of(17,30)              | LocalTime.of(19,30)
            LocalTime.of(17,30)              | LocalTime.of(18,30)
            LocalTime.of(18,30)              | LocalTime.of(19,30)
            LocalTime.of(18,0)               | LocalTime.of(19,0)
            LocalTime.of(17,0)               | LocalTime.of(19,0)
            LocalTime.of(18,0)               | LocalTime.of(20,0)
    }

    def "should not create when conflict schedules2"() {
        given:
            def schedule = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: "123",
                    startAt: LocalTime.of(17,30) ,
                    finishAt: LocalTime.of(19,30),
                    dayOfWeek: DayOfWeek.WEDNESDAY,
                    slotTime: 30
            )
            def scheduleSaved = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: "123",
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: DayOfWeek.WEDNESDAY,
                    slotTime: 30
            )
            scheduleService.createSchedule(scheduleSaved)
        when:
            scheduleService.createSchedule(schedule)
        then:
            1 * scheduleRepositoryMock.findByDoctorIdAndDayOfWeek(schedule.doctorId, schedule.dayOfWeek) >> [schedule]
            0 * scheduleRepositoryMock.save(schedule)
            thrown(ConflictException)

    }

    def "should not create when invalid slot"() {
        given:
            def schedule = new Schedule(
                    id: "1",
                    doctorName: "dr strange",
                    doctorId: "123",
                    startAt: LocalTime.of(18,0),
                    finishAt: LocalTime.of(19,0),
                    dayOfWeek: DayOfWeek.WEDNESDAY,
                    slotTime: 31
            )
        when:
            scheduleService.createSchedule(schedule)
        then:
            0 * scheduleRepositoryMock.findByDoctorIdAndDayOfWeek(schedule.doctorId, schedule.dayOfWeek) >> []
            0 * scheduleRepositoryMock.save(schedule)
            thrown(InvalidException)
    }
}
