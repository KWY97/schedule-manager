package com.example.manage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class MemberScheduleDetailResponse {

    private Long scheduleId;
    private LocalDate scheduleDate;
    private String course;
    private Integer groupNo;
    private String weather;
    private List<SpotResponse> spots;


    @Getter
    @AllArgsConstructor
    public static class SpotResponse {

        private Integer spotNo;
        private LocalTime startTime;
        private Integer sequence;
    }
}