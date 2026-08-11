package com.example.manage.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO(Data Transfer Object)
 *
 * 화면(HTML)에서 입력한 데이터를 Controller로 전달하기 위한 객체.
 *
 * Entity는 DB 테이블과 직접 연결되는 객체이고,
 * DTO는 화면 ↔ Controller ↔ Service 사이에서 데이터를 전달하는 용도로 사용한다.
 *
 * DTO를 사용하면
 * 1. Controller의 매개변수가 많아지는 것을 방지할 수 있다.
 * 2. Entity를 화면에 직접 노출하지 않아도 된다.
 * 3. 입력값 검증(@Valid) 등을 적용하기 쉽다.
 *
 * 현재는 일정 등록 화면에서 입력한 데이터를
 * 하나의 객체(ScheduleForm)로 받기 위해 사용한다.
 */

@Getter
@Setter
public class ScheduleForm {

    private Long memberId;
    private LocalDate scheduleDate;
    private String course;
    private Integer firstSpotNo;
    private LocalTime firstStartTime;
    private Integer secondSpotNo;
    private LocalTime secondStartTime;
}