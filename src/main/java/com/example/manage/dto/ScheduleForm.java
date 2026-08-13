package com.example.manage.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ScheduleForm {

    @NotNull(message = "참가자를 선택해 주세요.")
    private Long memberId;


    @NotNull(message = "일정 날짜를 선택해 주세요.")
    private LocalDate scheduleDate;


    @NotBlank(message = "코스를 선택해 주세요.")
    @Pattern(
            regexp = "^[ABC]$",
            message = "코스는 A, B, C 중 하나여야 합니다."
    )
    private String course;


    @NotNull(message = "첫 번째 스팟을 선택해 주세요.")
    @Min(
            value = 1,
            message = "첫 번째 스팟 번호는 1 이상이어야 합니다."
    )
    @Max(
            value = 6,
            message = "첫 번째 스팟 번호는 6 이하여야 합니다."
    )
    private Integer firstSpotNo;


    @NotNull(message = "첫 번째 시작 시간을 입력해 주세요.")
    private LocalTime firstStartTime;


    @NotNull(message = "두 번째 스팟을 선택해 주세요.")
    @Min(
            value = 1,
            message = "두 번째 스팟 번호는 1 이상이어야 합니다."
    )
    @Max(
            value = 6,
            message = "두 번째 스팟 번호는 6 이하여야 합니다."
    )
    private Integer secondSpotNo;


    @NotNull(message = "두 번째 시작 시간을 입력해 주세요.")
    private LocalTime secondStartTime;
}