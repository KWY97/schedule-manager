package com.example.manage.service;

import com.example.manage.domain.Member;
import com.example.manage.domain.Schedule;
import com.example.manage.domain.ScheduleSpot;
import com.example.manage.dto.MemberScheduleDetailResponse;
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

    @Transactional(readOnly = true)
    public MemberScheduleDetailResponse findMemberScheduleDetail(Long scheduleId, Long memberId) {

        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);

        if (schedule == null) {
            return null;
        }

        if (!schedule.getMember().getMemberId().equals(memberId)) {
            return null;
        }

        List<ScheduleSpot> scheduleSpots =
                scheduleSpotRepository
                        .findByScheduleScheduleIdOrderBySequenceAsc(scheduleId);

        List<MemberScheduleDetailResponse.SpotResponse> spots =
                scheduleSpots.stream()
                        .map(scheduleSpot ->
                                new MemberScheduleDetailResponse.SpotResponse(
                                        scheduleSpot.getSpotNo(),
                                        scheduleSpot.getStartTime(),
                                        scheduleSpot.getSequence()
                                )
                        )
                        .toList();

        return new MemberScheduleDetailResponse(
                schedule.getScheduleId(),
                schedule.getScheduleDate(),
                schedule.getCourse(),
                schedule.getMember().getGroupNo(),
                schedule.getWeather(),
                spots
        );
    }

    @Transactional
    public void updateSchedule(
            Long scheduleId,
            Long memberId,
            LocalDate scheduleDate,
            String course,
            Integer firstSpotNo,
            LocalTime firstStartTime,
            Integer secondSpotNo,
            LocalTime secondStartTime
    ) {
        /*
         * 수정할 일정 조회
         */
        Schedule schedule =
                scheduleRepository.findById(scheduleId)
                        .orElse(null);

        if (schedule == null) {
            return;
        }


        /*
         * 수정 화면에서 선택한 참가자 조회
         */
        Member member =
                memberRepository.findById(memberId)
                        .orElse(null);

        if (member == null) {
            return;
        }

        /*
         * 기존 ScheduleSpot 조회
         *
         * sequence 오름차순이므로
         * 0번 = 첫 번째 스팟
         * 1번 = 두 번째 스팟
         */
        List<ScheduleSpot> scheduleSpots =
                scheduleSpotRepository
                        .findByScheduleScheduleIdOrderBySequenceAsc(
                                scheduleId
                        );


        /*
         * 일정 기본 정보 수정
         */
        schedule.update(
                member,
                scheduleDate,
                course
        );

        /*
         * 첫 번째 스팟 수정
         */
        if (scheduleSpots.size() >= 1) {

            ScheduleSpot firstSpot =
                    scheduleSpots.get(0);

            firstSpot.update(
                    firstSpotNo,
                    firstStartTime
            );
        }

        /*
         * 두 번째 스팟 수정
         */
        if (scheduleSpots.size() >= 2) {

            ScheduleSpot secondSpot =
                    scheduleSpots.get(1);

            secondSpot.update(
                    secondSpotNo,
                    secondStartTime
            );
        }
    }
}
