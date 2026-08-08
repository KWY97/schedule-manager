package com.example.manage.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class ScheduleSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleSpotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(nullable = false)
    private Integer spotNo;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private Integer sequence;

    public ScheduleSpot(
            Schedule schedule,
            Integer spotNo,
            LocalTime startTime,
            Integer sequence)
    {
        this.schedule = schedule;
        this.spotNo = spotNo;
        this.startTime = startTime;
        this.sequence = sequence;
    }

    public void update(Integer spotNo, LocalTime startTime) {
        this.spotNo = spotNo;
        this.startTime = startTime;
    }
}
