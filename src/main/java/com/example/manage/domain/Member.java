package com.example.manage.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private Integer participantNo;

    @Column(name = "group_code", nullable = false)
    private String group;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    private String name;
    private String phone;

    public Member(Integer participantNo, String group, String loginId, String password) {
        this.participantNo = participantNo;
        this.group = group;
        this.loginId = loginId;
        this.password = password;
    }
}
