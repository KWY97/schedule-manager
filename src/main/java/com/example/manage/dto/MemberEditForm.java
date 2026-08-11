package com.example.manage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberEditForm {

    private Integer participantNo;
    private Integer groupNo;
    private String loginId;
    private String name;
    private String phone;
}
