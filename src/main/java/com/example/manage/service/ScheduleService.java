package com.example.manage.service;

import com.example.manage.domain.Member;
import com.example.manage.domain.Schedule;
import com.example.manage.domain.ScheduleSpot;
import com.example.manage.repository.MemberRepository;
import com.example.manage.repository.ScheduleRepository;
import com.example.manage.repository.ScheduleSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleSpotRepository scheduleSpotRepository;

    public void createSchedule(
            Long memberId,
            LocalDate scheduleDate,
            String course,
            Integer firstSpotNo,
            LocalTime firstStartTime,
            Integer secondSpotNo,
            LocalTime secondStartTime
    ) {
        Member member = memberRepository.findById(memberId).orElse(null);

        if (member == null) {
            return;
        }

        Schedule schedule = new Schedule(
                member,
                scheduleDate,
                course
        );

        scheduleRepository.save(schedule);

        ScheduleSpot firstSpot = new ScheduleSpot(
                schedule,
                firstSpotNo,
                firstStartTime,
                1
        );

        ScheduleSpot secondSpot = new ScheduleSpot(
                schedule,
                secondSpotNo,
                secondStartTime,
                2
        );

        scheduleSpotRepository.save(firstSpot);
        scheduleSpotRepository.save(secondSpot);
    }

    @Transactional(readOnly = true)
    public List<Schedule> findAllSchedules() {
        return scheduleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Schedule> findScheduleByMemberId(Long memberId) {
        return scheduleRepository.findByMemberMemberIdOrderByScheduleDateAsc(memberId);
    }

    @Transactional(readOnly = true)
    public Schedule findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ScheduleSpot> findScheduleSpots(Long scheduleId) {
        return scheduleSpotRepository.findByScheduleScheduleIdOrderBySequenceAsc(scheduleId);
    }
}
